package com.example.scanify.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.core.utils.DateUtils
import com.example.scanify.domain.usecase.GetAllGeneratedUseCase
import com.example.scanify.domain.usecase.GetAllScansUseCase
import com.example.scanify.domain.usecase.GetGeneratedCountByDateUseCase
import com.example.scanify.domain.usecase.GetGeneratedCountUseCase
import com.example.scanify.domain.usecase.GetGeneratedTypeDistributionUseCase
import com.example.scanify.domain.usecase.GetScansCountByDateUseCase
import com.example.scanify.domain.usecase.GetScanCountUseCase
import com.example.scanify.domain.usecase.GetScanTypeDistributionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getScanCountUseCase: GetScanCountUseCase,
    private val getGeneratedCountUseCase: GetGeneratedCountUseCase,
    private val getScansCountByDateUseCase: GetScansCountByDateUseCase,
    private val getGeneratedCountByDateUseCase: GetGeneratedCountByDateUseCase,
    private val getScanTypeDistributionUseCase: GetScanTypeDistributionUseCase,
    private val getGeneratedTypeDistributionUseCase: GetGeneratedTypeDistributionUseCase,
    getAllScansUseCase: GetAllScansUseCase,
    getAllGeneratedUseCase: GetAllGeneratedUseCase
) : ViewModel() {

    private val _totalScans = MutableStateFlow(0)
    val totalScans: StateFlow<Int> = _totalScans.asStateFlow()

    private val _totalGenerated = MutableStateFlow(0)
    val totalGenerated: StateFlow<Int> = _totalGenerated.asStateFlow()

    private val _scansToday = MutableStateFlow(0)
    val scansToday: StateFlow<Int> = _scansToday.asStateFlow()

    private val _generatedToday = MutableStateFlow(0)
    val generatedToday: StateFlow<Int> = _generatedToday.asStateFlow()

    private val _scanTypeDistribution = MutableStateFlow<Map<String, Int>>(emptyMap())
    val scanTypeDistribution: StateFlow<Map<String, Int>> = _scanTypeDistribution.asStateFlow()

    private val _generatedTypeDistribution = MutableStateFlow<Map<String, Int>>(emptyMap())
    val generatedTypeDistribution: StateFlow<Map<String, Int>> = _generatedTypeDistribution.asStateFlow()

    val totalFavorites: StateFlow<Int> = MutableStateFlow(0)

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _totalScans.value = getScanCountUseCase()
            _totalGenerated.value = getGeneratedCountUseCase()

            val startOfDay = DateUtils.getStartOfDay()
            val endOfDay = DateUtils.getEndOfDay()
            _scansToday.value = getScansCountByDateUseCase(startOfDay, endOfDay)
            _generatedToday.value = getGeneratedCountByDateUseCase(startOfDay, endOfDay)

            _scanTypeDistribution.value = getScanTypeDistributionUseCase()
            _generatedTypeDistribution.value = getGeneratedTypeDistributionUseCase()
        }
    }
}
