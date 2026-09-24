package com.example.scanify.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.model.ScanResult
import com.example.scanify.domain.usecase.DeleteGeneratedUseCase
import com.example.scanify.domain.usecase.DeleteScanUseCase
import com.example.scanify.domain.usecase.GetFavoritesUseCase
import com.example.scanify.domain.usecase.GetGeneratedFavoritesUseCase
import com.example.scanify.domain.usecase.SearchGeneratedUseCase
import com.example.scanify.domain.usecase.SearchScansUseCase
import com.example.scanify.domain.usecase.ToggleFavoriteUseCase
import com.example.scanify.domain.usecase.ToggleGeneratedFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val deleteScanUseCase: DeleteScanUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getGeneratedFavoritesUseCase: GetGeneratedFavoritesUseCase,
    private val deleteGeneratedUseCase: DeleteGeneratedUseCase,
    private val toggleGeneratedFavoriteUseCase: ToggleGeneratedFavoriteUseCase,
    private val searchScansUseCase: SearchScansUseCase,
    private val searchGeneratedUseCase: SearchGeneratedUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isGeneratedTab = MutableStateFlow(false)
    val isGeneratedTab = _isGeneratedTab.asStateFlow()

    private val _isMultiSelectMode = MutableStateFlow(false)
    val isMultiSelectMode = _isMultiSelectMode.asStateFlow()

    private val _selectedScanIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedScanIds = _selectedScanIds.asStateFlow()

    private val _selectedGeneratedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedGeneratedIds = _selectedGeneratedIds.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val favorites: StateFlow<List<ScanResult>> = combine(
        _searchQuery,
        getFavoritesUseCase()
    ) { query, favorites ->
        if (query.isEmpty()) favorites
        else favorites.filter {
            it.rawValue.contains(query, ignoreCase = true) ||
                    it.scanType.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val generatedFavorites: StateFlow<List<GeneratedQR>> = combine(
        _searchQuery,
        getGeneratedFavoritesUseCase()
    ) { query, favorites ->
        if (query.isEmpty()) favorites
        else favorites.filter {
            it.content.contains(query, ignoreCase = true) ||
                    it.type.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setGeneratedTab(isGenerated: Boolean) {
        _isGeneratedTab.value = isGenerated
        _searchQuery.value = ""
        exitMultiSelectMode()
    }

    fun toggleMultiSelectMode() {
        _isMultiSelectMode.value = !_isMultiSelectMode.value
        if (!_isMultiSelectMode.value) {
            _selectedScanIds.value = emptySet()
            _selectedGeneratedIds.value = emptySet()
        }
    }

    fun toggleScanSelection(id: Long) {
        val current = _selectedScanIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _selectedScanIds.value = current
    }

    fun toggleGeneratedSelection(id: Long) {
        val current = _selectedGeneratedIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _selectedGeneratedIds.value = current
    }

    fun selectAllFavorites() {
        _selectedScanIds.value = favorites.value.map { it.id }.toSet()
    }

    fun selectAllGeneratedFavorites() {
        _selectedGeneratedIds.value = generatedFavorites.value.map { it.id }.toSet()
    }

    fun exitMultiSelectMode() {
        _isMultiSelectMode.value = false
        _selectedScanIds.value = emptySet()
        _selectedGeneratedIds.value = emptySet()
    }

    fun deleteSelectedFavorites() {
        val ids = _selectedScanIds.value.toList()
        viewModelScope.launch {
            ids.forEach { id ->
                favorites.value.find { it.id == id }?.let { scan ->
                    deleteScanUseCase(scan)
                }
            }
            _selectedScanIds.value = emptySet()
        }
    }

    fun deleteSelectedGeneratedFavorites() {
        val ids = _selectedGeneratedIds.value.toList()
        viewModelScope.launch {
            ids.forEach { id ->
                generatedFavorites.value.find { it.id == id }?.let { qr ->
                    deleteGeneratedUseCase(qr)
                }
            }
            _selectedGeneratedIds.value = emptySet()
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
        }
    }

    fun toggleGeneratedFavorite(id: Long) {
        viewModelScope.launch {
            toggleGeneratedFavoriteUseCase(id)
        }
    }

    fun deleteScan(scan: ScanResult) {
        viewModelScope.launch {
            deleteScanUseCase(scan)
        }
    }

    fun deleteGenerated(qr: GeneratedQR) {
        viewModelScope.launch {
            deleteGeneratedUseCase(qr)
        }
    }

    fun setSelectedScanIds(ids: Set<Long>) {
        _selectedScanIds.value = ids
    }

    fun setSelectedGeneratedIds(ids: Set<Long>) {
        _selectedGeneratedIds.value = ids
    }
}
