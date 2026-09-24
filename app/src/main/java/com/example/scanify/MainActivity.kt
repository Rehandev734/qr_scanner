package com.example.scanify

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.scanify.data.datasource.SettingsDataStore
import com.example.scanify.presentation.navigation.ScanifyNavHost
import com.example.scanify.ui.theme.ScanifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        observeTheme()

        setContent {
            val themeMode by settingsDataStore.themeMode.collectAsStateWithLifecycle(initialValue = 0)
            ScanifyTheme(themeMode = themeMode) {
                ScanifyNavHost()
            }
        }
    }

    private fun observeTheme() {
        lifecycleScope.launch {
            settingsDataStore.themeMode.collectLatest { mode ->
                val nightMode = when (mode) {
                    1 -> AppCompatDelegate.MODE_NIGHT_NO
                    2 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                AppCompatDelegate.setDefaultNightMode(nightMode)
            }
        }
    }
}
