package com.example.scanify.data.repository

import com.example.scanify.data.datasource.SettingsDataStore
import com.example.scanify.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override val isDarkMode: Flow<Boolean> = dataStore.isDarkMode
    override val isSoundEnabled: Flow<Boolean> = dataStore.isSoundEnabled
    override val isVibrationEnabled: Flow<Boolean> = dataStore.isVibrationEnabled
    override val isAutoCopy: Flow<Boolean> = dataStore.isAutoCopy
    override val isAutoOpenUrl: Flow<Boolean> = dataStore.isAutoOpenUrl
    override val cameraLens: Flow<Int> = dataStore.cameraLens
    override val themeMode: Flow<Int> = dataStore.themeMode
    override val isContinuousScanEnabled: Flow<Boolean> = dataStore.isContinuousScanEnabled

    override suspend fun setDarkMode(enabled: Boolean) = dataStore.setDarkMode(enabled)
    override suspend fun setSoundEnabled(enabled: Boolean) = dataStore.setSoundEnabled(enabled)
    override suspend fun setVibrationEnabled(enabled: Boolean) = dataStore.setVibrationEnabled(enabled)
    override suspend fun setAutoCopy(enabled: Boolean) = dataStore.setAutoCopy(enabled)
    override suspend fun setAutoOpenUrl(enabled: Boolean) = dataStore.setAutoOpenUrl(enabled)
    override suspend fun setCameraLens(lens: Int) = dataStore.setCameraLens(lens)
    override suspend fun setThemeMode(mode: Int) = dataStore.setThemeMode(mode)
    override suspend fun setContinuousScan(enabled: Boolean) = dataStore.setContinuousScan(enabled)
}
