package com.example.scanify.presentation.generator

import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.core.qr.QRCodeGenerator
import com.example.scanify.core.qr.QRPresets
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.usecase.InsertGeneratedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    application: Application,
    private val insertGeneratedUseCase: InsertGeneratedUseCase
) : AndroidViewModel(application) {

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap = _qrBitmap.asStateFlow()

    private val _selectedType = MutableStateFlow(QRType.TEXT)
    val selectedType = _selectedType.asStateFlow()

    private val _config = MutableStateFlow(QRCodeGenerator.QRConfig())
    val config = _config.asStateFlow()

    private val _selectedPreset = MutableStateFlow(QRPresets.colorPresets[0])
    val selectedPreset = _selectedPreset.asStateFlow()

    private val _selectedTemplate = MutableStateFlow<QRPresets.Template?>(null)
    val selectedTemplate = _selectedTemplate.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError = _validationError.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private var previewJob: Job? = null
    private var lastGeneratedId: Long = 0

    fun setSelectedType(type: QRType) {
        _selectedType.value = type
    }

    fun setSelectedPreset(preset: QRPresets.ColorPreset) {
        _selectedPreset.value = preset
        _config.value = _config.value.copy(
            foregroundColor = preset.foreground,
            backgroundColor = preset.background,
            eyeColor = preset.eye
        )
        regeneratePreview()
    }

    fun setSelectedTemplate(template: QRPresets.Template) {
        _selectedTemplate.value = template
        _selectedType.value = QRType.valueOf(template.type)
        _config.value = _config.value.copy(
            moduleStyle = template.moduleStyle,
            eyeStyle = template.eyeStyle,
            foregroundColor = template.colorPreset.foreground,
            backgroundColor = template.colorPreset.background,
            eyeColor = template.colorPreset.eye
        )
    }

    fun updateForegroundColor(color: Int) {
        _config.value = _config.value.copy(foregroundColor = color)
        regeneratePreview()
    }

    fun updateBackgroundColor(color: Int) {
        _config.value = _config.value.copy(backgroundColor = color)
        regeneratePreview()
    }

    fun updateEyeColor(color: Int) {
        _config.value = _config.value.copy(eyeColor = color)
        regeneratePreview()
    }

    fun updateModuleStyle(style: QRCodeGenerator.ModuleStyle) {
        _config.value = _config.value.copy(moduleStyle = style)
        regeneratePreview()
    }

    fun updateEyeStyle(style: QRCodeGenerator.EyeStyle) {
        _config.value = _config.value.copy(eyeStyle = style)
        regeneratePreview()
    }

    fun updateGradient(enabled: Boolean, startColor: Int = _config.value.gradientStartColor,
                       endColor: Int = _config.value.gradientEndColor,
                       type: QRCodeGenerator.GradientType = _config.value.gradientType) {
        _config.value = _config.value.copy(
            gradientEnabled = enabled,
            gradientStartColor = startColor,
            gradientEndColor = endColor,
            gradientType = type
        )
        regeneratePreview()
    }

    fun updateLogo(logo: Bitmap?) {
        _config.value = _config.value.copy(logo = logo)
        regeneratePreview()
    }

    fun setLogoPath(path: String) {
        _config.value = _config.value.copy(logoPath = path)
    }

    fun updateFrame(style: String, text: String) {
        _config.value = _config.value.copy(frameStyle = style, frameText = text)
        regeneratePreview()
    }

    fun generatePreview(content: String) {
        if (content.isBlank()) {
            _qrBitmap.value = null
            return
        }
        _validationError.value = validateInput(content)
        if (_validationError.value != null) return

        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            _isGenerating.value = true
            delay(300)
            val bitmap = withContext(Dispatchers.Default) {
                QRCodeGenerator.generate(_config.value.copy(content = content))
            }
            _qrBitmap.value = bitmap
            _isGenerating.value = false
        }
    }

    private fun regeneratePreview() {
        val currentContent = _config.value.content
        if (currentContent.isNotBlank()) {
            generatePreview(currentContent)
        }
    }

    fun saveToHistory(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val id = insertGeneratedUseCase(
                GeneratedQR(
                    content = content,
                    type = _selectedType.value.name,
                    timestamp = System.currentTimeMillis(),
                    foregroundColor = _config.value.foregroundColor,
                    backgroundColor = _config.value.backgroundColor,
                    eyeColor = _config.value.eyeColor,
                    moduleStyle = _config.value.moduleStyle.name,
                    eyeStyle = _config.value.eyeStyle.name,
                    gradientEnabled = _config.value.gradientEnabled,
                    gradientStartColor = _config.value.gradientStartColor,
                    gradientEndColor = _config.value.gradientEndColor,
                    gradientType = _config.value.gradientType.name,
                    logoPath = _config.value.logoPath,
                    frameStyle = _config.value.frameStyle,
                    frameText = _config.value.frameText,
                    templateName = _selectedTemplate.value?.name ?: ""
                )
            )
            lastGeneratedId = id
            if (id <= 0L) {
                Toast.makeText(getApplication(), "Failed to save QR code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveBitmap(bitmap: Bitmap, format: String = "PNG", quality: Int = 100): Boolean {
        return try {
            val context = getApplication<Application>()
            val filename = "QR_${System.currentTimeMillis()}.${format.lowercase()}"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: context.cacheDir
            val file = File(storageDir, filename)
            FileOutputStream(file).use { out ->
                val compressFormat = when (format.uppercase()) {
                    "JPEG" -> Bitmap.CompressFormat.JPEG
                    else -> Bitmap.CompressFormat.PNG
                }
                bitmap.compress(compressFormat, quality, out)
            }
            Toast.makeText(getApplication(), "Saved to Pictures folder", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Failed to save", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun formatContent(type: QRType, text: String, ssid: String = "", password: String = "",
                      email: String = "", phone: String = "", subject: String = "", body: String = "",
                      latitude: String = "", longitude: String = "",
                      name: String = "", org: String = "", url: String = ""): String {
        return QRCodeGenerator.formatContent(
            type.name, text, ssid, password, email, phone, subject, body, latitude, longitude, name, org, url
        )
    }

    private fun validateInput(content: String): String? {
        return when (_selectedType.value) {
            QRType.WEBSITE -> {
                if (!content.startsWith("http") && !content.startsWith("www.")) {
                    "Please enter a valid URL"
                } else null
            }
            QRType.EMAIL -> {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(content).matches()) {
                    "Please enter a valid email address"
                } else null
            }
            QRType.PHONE -> {
                if (!android.util.Patterns.PHONE.matcher(content).matches()) {
                    "Please enter a valid phone number"
                } else null
            }
            else -> if (content.isBlank()) "Content cannot be empty" else null
        }
    }

    fun loadFromHistory(qr: GeneratedQR) {
        _selectedType.value = QRType.valueOf(qr.type)
        _config.value = QRCodeGenerator.QRConfig(
            content = qr.content,
            foregroundColor = qr.foregroundColor,
            backgroundColor = qr.backgroundColor,
            eyeColor = qr.eyeColor,
            moduleStyle = QRCodeGenerator.ModuleStyle.valueOf(qr.moduleStyle),
            eyeStyle = QRCodeGenerator.EyeStyle.valueOf(qr.eyeStyle),
            gradientEnabled = qr.gradientEnabled,
            gradientStartColor = qr.gradientStartColor,
            gradientEndColor = qr.gradientEndColor,
            gradientType = QRCodeGenerator.GradientType.valueOf(qr.gradientType),
            logoPath = qr.logoPath,
            frameStyle = qr.frameStyle,
            frameText = qr.frameText
        )
        generatePreview(qr.content)
    }

    fun resetConfig() {
        _config.value = QRCodeGenerator.QRConfig()
        _selectedPreset.value = QRPresets.colorPresets[0]
        _selectedTemplate.value = null
    }
}

enum class QRType {
    TEXT, WEBSITE, EMAIL, PHONE, SMS, WIFI, CONTACT, LOCATION, CALENDAR
}
