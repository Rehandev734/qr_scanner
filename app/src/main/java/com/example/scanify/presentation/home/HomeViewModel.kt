package com.example.scanify.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.core.utils.DateUtils
import com.example.scanify.domain.usecase.GetAllGeneratedUseCase
import com.example.scanify.domain.usecase.GetAllScansUseCase
import com.example.scanify.domain.usecase.GetRecentGeneratedUseCase
import com.example.scanify.domain.usecase.GetRecentScansUseCase
import com.example.scanify.domain.usecase.GetScansCountByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentScansUseCase: GetRecentScansUseCase,
    private val getRecentGeneratedUseCase: GetRecentGeneratedUseCase,
    private val getAllScansUseCase: GetAllScansUseCase,
    private val getAllGeneratedUseCase: GetAllGeneratedUseCase,
    private val getScansCountByDateUseCase: GetScansCountByDateUseCase
) : ViewModel() {

    private val _recentActivity = MutableStateFlow<List<RecentActivity>>(emptyList())
    val recentActivity = _recentActivity.asStateFlow()

    private val _stats = MutableStateFlow(HomeStats())
    val stats = _stats.asStateFlow()

    init {
        loadRecentActivity()
        loadStatistics()
    }

    fun loadRecentActivity() {
        viewModelScope.launch {
            val scans = getRecentScansUseCase(5)
            val generated = getRecentGeneratedUseCase(5)

            val allActivity = scans.map { RecentActivity.Scanned(it) } +
                    generated.map { RecentActivity.Generated(it) }

            _recentActivity.value = allActivity
                .sortedByDescending { it.timestamp }
                .take(5)
        }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                var totalFavorites = 0
                var totalScans = 0
                var totalGenerated = 0

                getAllScansUseCase().collect { scans ->
                    totalScans = scans.size
                    totalFavorites = scans.count { it.isFavorite }

                    getAllGeneratedUseCase().collect { generated ->
                        totalGenerated = generated.size
                        totalFavorites += generated.count { it.isFavorite }

                        val todayStart = DateUtils.getStartOfDay(System.currentTimeMillis())
                        val todayEnd = DateUtils.getEndOfDay(System.currentTimeMillis())
                        val todayScans = getScansCountByDateUseCase(todayStart, todayEnd)

                        _stats.value = HomeStats(
                            totalScans = totalScans,
                            totalGenerated = totalGenerated,
                            totalFavorites = totalFavorites,
                            todayActivity = todayScans
                        )
                    }
                }
            } catch (_: Exception) {
                // Statistics loading failed silently
            }
        }
    }
}

data class HomeStats(
    val totalScans: Int = 0,
    val totalGenerated: Int = 0,
    val totalFavorites: Int = 0,
    val todayActivity: Int = 0
)
