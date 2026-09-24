package com.example.scanify.presentation.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.data.datasource.SettingsDataStore
import com.example.scanify.domain.model.ScanResult
import com.example.scanify.domain.usecase.DeleteScanUseCase
import com.example.scanify.domain.usecase.GetScanByIdUseCase
import com.example.scanify.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    application: Application,
    private val getScanByIdUseCase: GetScanByIdUseCase,
    private val deleteScanUseCase: DeleteScanUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val settingsDataStore: SettingsDataStore
) : AndroidViewModel(application) {

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult = _scanResult.asStateFlow()

    val isAutoCopy = settingsDataStore.isAutoCopy
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isAutoOpenUrl = settingsDataStore.isAutoOpenUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun loadScan(id: Long) {
        viewModelScope.launch {
            _scanResult.value = getScanByIdUseCase(id)
        }
    }

    fun deleteScan(scan: ScanResult) {
        viewModelScope.launch {
            deleteScanUseCase(scan)
            _scanResult.value = null
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
            _scanResult.value = getScanByIdUseCase(id)
        }
    }
}
