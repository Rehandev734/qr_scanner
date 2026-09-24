package com.example.scanify.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>
    val isSoundEnabled: Flow<Boolean>
    val isVibrationEnabled: Flow<Boolean>
    val isAutoCopy: Flow<Boolean>
    val isAutoOpenUrl: Flow<Boolean>
    val cameraLens: Flow<Int>
    val themeMode: Flow<Int>
    val isContinuousScanEnabled: Flow<Boolean>

    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setAutoCopy(enabled: Boolean)
    suspend fun setAutoOpenUrl(enabled: Boolean)
    suspend fun setCameraLens(lens: Int)
    suspend fun setThemeMode(mode: Int)
    suspend fun setContinuousScan(enabled: Boolean)
}
