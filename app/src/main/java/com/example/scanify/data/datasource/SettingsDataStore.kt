package com.example.scanify.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        private val KEY_VIBRATION = booleanPreferencesKey("vibration_enabled")
        private val KEY_AUTO_COPY = booleanPreferencesKey("auto_copy")
        private val KEY_AUTO_OPEN_URL = booleanPreferencesKey("auto_open_url")
        private val KEY_CAMERA_LENS = intPreferencesKey("camera_lens")
        private val KEY_THEME_MODE = intPreferencesKey("theme_mode")
        private val KEY_CONTINUOUS_SCAN = booleanPreferencesKey("continuous_scan")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK_MODE] ?: false }
    val isSoundEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SOUND] ?: true }
    val isVibrationEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_VIBRATION] ?: true }
    val isAutoCopy: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_COPY] ?: false }
    val isAutoOpenUrl: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_OPEN_URL] ?: false }
    val cameraLens: Flow<Int> = context.dataStore.data.map { it[KEY_CAMERA_LENS] ?: 0 }
    val themeMode: Flow<Int> = context.dataStore.data.map { it[KEY_THEME_MODE] ?: 0 }
    val isContinuousScanEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_CONTINUOUS_SCAN] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION] = enabled }
    }

    suspend fun setAutoCopy(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_COPY] = enabled }
    }

    suspend fun setAutoOpenUrl(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_OPEN_URL] = enabled }
    }

    suspend fun setCameraLens(lens: Int) {
        context.dataStore.edit { it[KEY_CAMERA_LENS] = lens }
    }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setContinuousScan(enabled: Boolean) {
        context.dataStore.edit { it[KEY_CONTINUOUS_SCAN] = enabled }
    }
}
