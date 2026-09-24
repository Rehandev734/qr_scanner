package com.example.scanify.domain.usecase

import com.example.scanify.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    val isDarkMode: Flow<Boolean> get() = repository.isDarkMode
    val isSoundEnabled: Flow<Boolean> get() = repository.isSoundEnabled
    val isVibrationEnabled: Flow<Boolean> get() = repository.isVibrationEnabled
    val isAutoCopy: Flow<Boolean> get() = repository.isAutoCopy
    val isAutoOpenUrl: Flow<Boolean> get() = repository.isAutoOpenUrl
    val cameraLens: Flow<Int> get() = repository.cameraLens
    val themeMode: Flow<Int> get() = repository.themeMode
    val isContinuousScanEnabled: Flow<Boolean> get() = repository.isContinuousScanEnabled
}

class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend fun setDarkMode(enabled: Boolean) = repository.setDarkMode(enabled)
    suspend fun setSoundEnabled(enabled: Boolean) = repository.setSoundEnabled(enabled)
    suspend fun setVibrationEnabled(enabled: Boolean) = repository.setVibrationEnabled(enabled)
    suspend fun setAutoCopy(enabled: Boolean) = repository.setAutoCopy(enabled)
    suspend fun setAutoOpenUrl(enabled: Boolean) = repository.setAutoOpenUrl(enabled)
    suspend fun setCameraLens(lens: Int) = repository.setCameraLens(lens)
    suspend fun setThemeMode(mode: Int) = repository.setThemeMode(mode)
    suspend fun setContinuousScan(enabled: Boolean) = repository.setContinuousScan(enabled)
}
