package com.example.scanify.presentation.generated

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.usecase.DeleteAllGeneratedUseCase
import com.example.scanify.domain.usecase.DeleteGeneratedUseCase
import com.example.scanify.domain.usecase.GetAllGeneratedUseCase
import com.example.scanify.domain.usecase.GetGeneratedFavoritesUseCase
import com.example.scanify.domain.usecase.SearchGeneratedUseCase
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
class GeneratedHistoryViewModel @Inject constructor(
    private val getAllGeneratedUseCase: GetAllGeneratedUseCase,
    private val getGeneratedFavoritesUseCase: GetGeneratedFavoritesUseCase,
    private val searchGeneratedUseCase: SearchGeneratedUseCase,
    private val deleteGeneratedUseCase: DeleteGeneratedUseCase,
    private val deleteAllGeneratedUseCase: DeleteAllGeneratedUseCase,
    private val toggleGeneratedFavoriteUseCase: ToggleGeneratedFavoriteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly = _showFavoritesOnly.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val generatedItems: StateFlow<List<GeneratedQR>> = combine(
        _searchQuery,
        _showFavoritesOnly
    ) { query, showFavorites ->
        Pair(query, showFavorites)
    }.flatMapLatest { (query, showFavorites) ->
        when {
            query.isNotEmpty() -> searchGeneratedUseCase(query)
            showFavorites -> getGeneratedFavoritesUseCase()
            else -> getAllGeneratedUseCase()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleShowFavorites() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            toggleGeneratedFavoriteUseCase(id)
        }
    }

    fun deleteGenerated(qr: GeneratedQR) {
        viewModelScope.launch {
            deleteGeneratedUseCase(qr)
        }
    }

    fun deleteAllGenerated() {
        viewModelScope.launch {
            deleteAllGeneratedUseCase()
        }
    }
}
