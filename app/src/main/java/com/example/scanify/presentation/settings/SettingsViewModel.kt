package com.example.scanify.presentation.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.domain.usecase.GetSettingsUseCase
import com.example.scanify.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : AndroidViewModel(application) {

    val isDarkMode = getSettingsUseCase.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isSoundEnabled = getSettingsUseCase.isSoundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isVibrationEnabled = getSettingsUseCase.isVibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isAutoCopy = getSettingsUseCase.isAutoCopy
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isAutoOpenUrl = getSettingsUseCase.isAutoOpenUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val themeMode = getSettingsUseCase.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isContinuousScanEnabled = getSettingsUseCase.isContinuousScanEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setDarkMode(enabled) }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch { updateSettingsUseCase.setThemeMode(mode) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setSoundEnabled(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setVibrationEnabled(enabled) }
    }

    fun setAutoCopy(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setAutoCopy(enabled) }
    }

    fun setAutoOpenUrl(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setAutoOpenUrl(enabled) }
    }

    fun setContinuousScan(enabled: Boolean) {
        viewModelScope.launch { updateSettingsUseCase.setContinuousScan(enabled) }
    }
}
