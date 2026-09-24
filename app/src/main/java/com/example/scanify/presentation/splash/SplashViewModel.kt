package com.example.scanify.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome = _navigateToHome.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _navigateToHome.value = true
        }
    }

    fun consumeNavigation() {
        _navigateToHome.value = false
    }
}
