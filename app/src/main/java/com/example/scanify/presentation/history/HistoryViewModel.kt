package com.example.scanify.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.model.ScanResult
import com.example.scanify.domain.usecase.AddFavoritesByIdsUseCase
import com.example.scanify.domain.usecase.DeleteGeneratedByIdsUseCase
import com.example.scanify.domain.usecase.DeleteGeneratedUseCase
import com.example.scanify.domain.usecase.DeleteScanUseCase
import com.example.scanify.domain.usecase.DeleteScansByIdsUseCase
import com.example.scanify.domain.usecase.GetAllGeneratedUseCase
import com.example.scanify.domain.usecase.GetAllScansUseCase
import com.example.scanify.domain.usecase.GetFavoritesUseCase
import com.example.scanify.domain.usecase.GetGeneratedFavoritesUseCase
import com.example.scanify.domain.usecase.RemoveFavoritesByIdsUseCase
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
class HistoryViewModel @Inject constructor(
    private val getAllScansUseCase: GetAllScansUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val searchScansUseCase: SearchScansUseCase,
    private val deleteScanUseCase: DeleteScanUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getAllGeneratedUseCase: GetAllGeneratedUseCase,
    private val getGeneratedFavoritesUseCase: GetGeneratedFavoritesUseCase,
    private val searchGeneratedUseCase: SearchGeneratedUseCase,
    private val deleteGeneratedUseCase: DeleteGeneratedUseCase,
    private val toggleGeneratedFavoriteUseCase: ToggleGeneratedFavoriteUseCase,
    private val deleteScansByIdsUseCase: DeleteScansByIdsUseCase,
    private val deleteGeneratedByIdsUseCase: DeleteGeneratedByIdsUseCase,
    private val addFavoritesByIdsUseCase: AddFavoritesByIdsUseCase,
    private val removeFavoritesByIdsUseCase: RemoveFavoritesByIdsUseCase,
    private val addGeneratedFavoritesByIdsUseCase: AddFavoritesByIdsUseCase? = null,
    private val removeGeneratedFavoritesByIdsUseCase: RemoveFavoritesByIdsUseCase? = null
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly = _showFavoritesOnly.asStateFlow()

    private val _isGeneratedTab = MutableStateFlow(false)
    val isGeneratedTab = _isGeneratedTab.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.NEWEST_FIRST)
    val sortOption = _sortOption.asStateFlow()

    private val _filterOption = MutableStateFlow(FilterOption.ALL)
    val filterOption = _filterOption.asStateFlow()

    private val _isMultiSelectMode = MutableStateFlow(false)
    val isMultiSelectMode = _isMultiSelectMode.asStateFlow()

    private val _selectedScanIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedScanIds = _selectedScanIds.asStateFlow()

    private val _selectedGeneratedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedGeneratedIds = _selectedGeneratedIds.asStateFlow()

    private val _pendingDeletedScans = MutableStateFlow<List<ScanResult>>(emptyList())
    private val _pendingDeletedGenerated = MutableStateFlow<List<GeneratedQR>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val scans: StateFlow<List<ScanResult>> = combine(
        _searchQuery,
        _showFavoritesOnly,
        _sortOption,
        _filterOption
    ) { query, showFavorites, sort, filter ->
        SearchParams(query, showFavorites, sort, filter)
    }.flatMapLatest { params ->
        when {
            params.query.isNotEmpty() -> searchScansUseCase(params.query)
            params.showFavorites -> getFavoritesUseCase()
            params.filter != FilterOption.ALL -> {
                when (params.filter) {
                    FilterOption.FAVORITES -> getFavoritesUseCase()
                    FilterOption.URLS -> searchScansUseCase("URL")
                    FilterOption.WIFI -> searchScansUseCase("WIFI")
                    FilterOption.CONTACT -> searchScansUseCase("CONTACT")
                    FilterOption.EMAIL -> searchScansUseCase("EMAIL")
                    FilterOption.SMS -> searchScansUseCase("SMS")
                    FilterOption.PHONE -> searchScansUseCase("PHONE")
                    FilterOption.LOCATION -> searchScansUseCase("GEO")
                    FilterOption.TEXT -> searchScansUseCase("TEXT")
                    else -> getAllScansUseCase()
                }
            }
            else -> getAllScansUseCase()
        }
    }.combine(_sortOption) { scans, sort ->
        when (sort) {
            SortOption.NEWEST_FIRST -> scans.sortedByDescending { it.timestamp }
            SortOption.OLDEST_FIRST -> scans.sortedBy { it.timestamp }
            SortOption.ALPHABETICAL -> scans.sortedBy { it.rawValue.lowercase() }
            SortOption.TYPE -> scans.sortedBy { it.scanType }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val generatedItems: StateFlow<List<GeneratedQR>> = combine(
        _searchQuery,
        _showFavoritesOnly,
        _sortOption,
        _filterOption
    ) { query, showFavorites, sort, filter ->
        SearchParams(query, showFavorites, sort, filter)
    }.flatMapLatest { params ->
        when {
            params.query.isNotEmpty() -> searchGeneratedUseCase(params.query)
            params.showFavorites -> getGeneratedFavoritesUseCase()
            params.filter == FilterOption.FAVORITES -> getGeneratedFavoritesUseCase()
            else -> getAllGeneratedUseCase()
        }
    }.combine(_sortOption) { items, sort ->
        when (sort) {
            SortOption.NEWEST_FIRST -> items.sortedByDescending { it.timestamp }
            SortOption.OLDEST_FIRST -> items.sortedBy { it.timestamp }
            SortOption.ALPHABETICAL -> items.sortedBy { it.content.lowercase() }
            SortOption.TYPE -> items.sortedBy { it.type }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleShowFavorites() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun setGeneratedTab(isGenerated: Boolean) {
        _isGeneratedTab.value = isGenerated
        _searchQuery.value = ""
        _showFavoritesOnly.value = false
        _filterOption.value = FilterOption.ALL
        exitMultiSelectMode()
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun setFilterOption(option: FilterOption) {
        _filterOption.value = option
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

    fun selectAllScans() {
        _selectedScanIds.value = scans.value.map { it.id }.toSet()
    }

    fun selectAllGenerated() {
        _selectedGeneratedIds.value = generatedItems.value.map { it.id }.toSet()
    }

    fun exitMultiSelectMode() {
        _isMultiSelectMode.value = false
        _selectedScanIds.value = emptySet()
        _selectedGeneratedIds.value = emptySet()
    }

    fun deleteSelectedScans() {
        val ids = _selectedScanIds.value.toList()
        if (ids.isEmpty()) return
        val deletedItems = scans.value.filter { it.id in ids }
        _pendingDeletedScans.value = deletedItems
        viewModelScope.launch {
            deleteScansByIdsUseCase(ids)
            _selectedScanIds.value = emptySet()
        }
    }

    fun deleteSelectedGenerated() {
        val ids = _selectedGeneratedIds.value.toList()
        if (ids.isEmpty()) return
        val deletedItems = generatedItems.value.filter { it.id in ids }
        _pendingDeletedGenerated.value = deletedItems
        viewModelScope.launch {
            deleteGeneratedByIdsUseCase(ids)
            _selectedGeneratedIds.value = emptySet()
        }
    }

    fun undoDeleteScans() {
        viewModelScope.launch {
            _pendingDeletedScans.value.forEach { scan ->
                com.example.scanify.domain.usecase.InsertScanUseCase::class.java
            }
            _pendingDeletedScans.value = emptyList()
        }
    }

    fun addSelectedToFavorites() {
        val ids = _selectedScanIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            addFavoritesByIdsUseCase(ids)
            _selectedScanIds.value = emptySet()
            _isMultiSelectMode.value = false
        }
    }

    fun removeSelectedFromFavorites() {
        val ids = _selectedScanIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            removeFavoritesByIdsUseCase(ids)
            _selectedScanIds.value = emptySet()
            _isMultiSelectMode.value = false
        }
    }

    fun deleteScan(scan: ScanResult) {
        viewModelScope.launch {
            deleteScanUseCase(scan)
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
        }
    }

    fun deleteGenerated(qr: GeneratedQR) {
        viewModelScope.launch {
            deleteGeneratedUseCase(qr)
        }
    }

    fun toggleGeneratedFavorite(id: Long) {
        viewModelScope.launch {
            toggleGeneratedFavoriteUseCase(id)
        }
    }

    fun deleteAllScans() {
        viewModelScope.launch {
            val allScans = scans.value
            _pendingDeletedScans.value = allScans
            allScans.forEach { scan ->
                deleteScanUseCase(scan)
            }
        }
    }

    fun deleteAllGenerated() {
        viewModelScope.launch {
            val allGenerated = generatedItems.value
            _pendingDeletedGenerated.value = allGenerated
            allGenerated.forEach { qr ->
                deleteGeneratedUseCase(qr)
            }
        }
    }

    fun setSelectedScanIds(ids: Set<Long>) {
        _selectedScanIds.value = ids
    }

    fun setSelectedGeneratedIds(ids: Set<Long>) {
        _selectedGeneratedIds.value = ids
    }
}

enum class SortOption {
    NEWEST_FIRST,
    OLDEST_FIRST,
    ALPHABETICAL,
    TYPE
}

enum class FilterOption {
    ALL,
    SCANNED,
    GENERATED,
    FAVORITES,
    URLS,
    WIFI,
    CONTACT,
    EMAIL,
    SMS,
    PHONE,
    LOCATION,
    TEXT,
    CALENDAR,
    PRODUCT_BARCODE
}

data class SearchParams(
    val query: String,
    val showFavorites: Boolean,
    val sort: SortOption,
    val filter: FilterOption
)
