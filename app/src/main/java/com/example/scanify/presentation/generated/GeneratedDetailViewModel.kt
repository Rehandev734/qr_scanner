package com.example.scanify.presentation.generated

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.core.qr.QRCodeGenerator
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.usecase.DeleteGeneratedUseCase
import com.example.scanify.domain.usecase.GetGeneratedByIdUseCase
import com.example.scanify.domain.usecase.ToggleGeneratedFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class GeneratedDetailViewModel @Inject constructor(
    application: Application,
    private val getGeneratedByIdUseCase: GetGeneratedByIdUseCase,
    private val deleteGeneratedUseCase: DeleteGeneratedUseCase,
    private val toggleGeneratedFavoriteUseCase: ToggleGeneratedFavoriteUseCase
) : AndroidViewModel(application) {

    private val _generatedQR = MutableStateFlow<GeneratedQR?>(null)
    val generatedQR = _generatedQR.asStateFlow()

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap = _qrBitmap.asStateFlow()

    fun loadGenerated(id: Long) {
        viewModelScope.launch {
            val qr = getGeneratedByIdUseCase(id)
            _generatedQR.value = qr
            qr?.let { generateBitmap(it) }
        }
    }

    fun loadFromArguments(content: String, type: String, timestamp: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            val qr = GeneratedQR(
                id = 0,
                content = content,
                type = type,
                timestamp = timestamp,
                isFavorite = isFavorite
            )
            _generatedQR.value = qr
            generateBitmap(qr)
        }
    }

    private fun generateBitmap(qr: GeneratedQR) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                val config = QRCodeGenerator.QRConfig(
                    content = qr.content,
                    foregroundColor = if (qr.foregroundColor != 0) qr.foregroundColor else Color.BLACK,
                    backgroundColor = if (qr.backgroundColor != 0) qr.backgroundColor else Color.WHITE,
                    eyeColor = if (qr.eyeColor != 0) qr.eyeColor else qr.foregroundColor,
                    moduleStyle = try {
                        QRCodeGenerator.ModuleStyle.valueOf(qr.moduleStyle)
                    } catch (_: Exception) {
                        QRCodeGenerator.ModuleStyle.SQUARE
                    },
                    eyeStyle = try {
                        QRCodeGenerator.EyeStyle.valueOf(qr.eyeStyle)
                    } catch (_: Exception) {
                        QRCodeGenerator.EyeStyle.SQUARE
                    },
                    gradientEnabled = qr.gradientEnabled,
                    gradientStartColor = qr.gradientStartColor,
                    gradientEndColor = qr.gradientEndColor,
                    gradientType = try {
                        QRCodeGenerator.GradientType.valueOf(qr.gradientType)
                    } catch (_: Exception) {
                        QRCodeGenerator.GradientType.LINEAR
                    }
                )
                QRCodeGenerator.generate(config)
            }
            _qrBitmap.value = bitmap
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            toggleGeneratedFavoriteUseCase(id)
            val updated = getGeneratedByIdUseCase(id)
            _generatedQR.value = updated
        }
    }

    fun deleteGenerated() {
        viewModelScope.launch {
            _generatedQR.value?.let {
                deleteGeneratedUseCase(it)
            }
        }
    }

    fun saveBitmap(bitmap: Bitmap) {
        try {
            val context = getApplication<Application>()
            val filename = "QR_${System.currentTimeMillis()}.png"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: context.cacheDir
            val file = File(storageDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Toast.makeText(getApplication(), "Saved to Pictures folder", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(getApplication(), "Failed to save", Toast.LENGTH_SHORT).show()
        }
    }
}
