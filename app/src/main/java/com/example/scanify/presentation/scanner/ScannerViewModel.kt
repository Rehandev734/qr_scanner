package com.example.scanify.presentation.scanner

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.data.datasource.SettingsDataStore
import com.example.scanify.data.scanner.BarcodeMapper
import com.example.scanify.domain.model.ScanResult
import com.example.scanify.domain.usecase.InsertScanUseCase
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    application: Application,
    private val insertScanUseCase: InsertScanUseCase,
    private val settingsDataStore: SettingsDataStore
) : AndroidViewModel(application) {

    private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanState = _scanState.asStateFlow()

    private val _isContinuousScan = MutableStateFlow(false)
    val isContinuousScan = _isContinuousScan.asStateFlow()

    private val _zoomLevel = MutableStateFlow(1f)
    val zoomLevel = _zoomLevel.asStateFlow()

    private val _detectedBarcodes = MutableStateFlow<List<Barcode>>(emptyList())
    val detectedBarcodes = _detectedBarcodes.asStateFlow()

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn = _isFlashOn.asStateFlow()

    private val _currentTipIndex = MutableStateFlow(0)
    val currentTipIndex = _currentTipIndex.asStateFlow()

    val scanTips = listOf(
        "Point your camera at a QR code or barcode",
        "Hold steady for faster detection",
        "Use pinch to zoom for distant codes",
        "Tap the flashlight in low light conditions",
        "Scan from gallery to import images"
    )

    val isSoundEnabled = settingsDataStore.isSoundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isVibrationEnabled = settingsDataStore.isVibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private var isProcessing = false
    private var lastScanTime = 0L
    private var lastScanValue = ""

    companion object {
        private const val SCAN_COOLDOWN_MS = 1500L
    }

    init {
        viewModelScope.launch {
            settingsDataStore.isContinuousScanEnabled.collect { _isContinuousScan.value = it }
        }
    }

    fun onBarcodeDetected(barcodes: List<Barcode>) {
        if (isProcessing) return
        if (barcodes.isEmpty()) return

        val now = System.currentTimeMillis()
        if (now - lastScanTime < SCAN_COOLDOWN_MS) return

        val primary = barcodes.first()
        val rawValue = BarcodeMapper.extractValue(primary)
        if (rawValue == lastScanValue && now - lastScanTime < SCAN_COOLDOWN_MS * 3) return

        lastScanTime = now
        lastScanValue = rawValue

        if (barcodes.size > 1) {
            _detectedBarcodes.value = barcodes
            _scanState.value = ScanUiState.MultipleFound(barcodes)
            return
        }

        processSingleBarcode(primary)
    }

    fun processSingleBarcode(barcode: Barcode) {
        if (isProcessing) return
        isProcessing = true
        _scanState.value = ScanUiState.Processing

        val rawValue = BarcodeMapper.extractValue(barcode)
        val formatName = BarcodeMapper.getFormatName(barcode.format)
        val typeName = BarcodeMapper.formatType(barcode)

        viewModelScope.launch {
            val scan = ScanResult(
                rawValue = rawValue,
                format = formatName,
                scanType = typeName,
                timestamp = System.currentTimeMillis()
            )

            val id = insertScanUseCase(scan)
            _scanState.value = ScanUiState.Success(
                scan.copy(id = id)
            )
        }
    }

    fun setZoomLevel(level: Float) {
        _zoomLevel.value = level.coerceIn(1f, 5f)
    }

    fun toggleFlash() {
        _isFlashOn.value = !_isFlashOn.value
    }

    fun cycleTipIndex() {
        _currentTipIndex.value = (_currentTipIndex.value + 1) % scanTips.size
    }

    fun resetState() {
        _scanState.value = ScanUiState.Idle
        isProcessing = false
    }

    fun onError(message: String) {
        _scanState.value = ScanUiState.Error(message)
        isProcessing = false
    }

    fun playBeepSound() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
            toneGen.release()
        } catch (_: Exception) { }
    }

    fun vibrate() {
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator?.vibrate(
                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } catch (_: Exception) { }
    }
}

sealed class ScanUiState {
    data object Idle : ScanUiState()
    data object Processing : ScanUiState()
    data class Success(val scan: ScanResult) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
    data class MultipleFound(val barcodes: List<Barcode>) : ScanUiState()
}
