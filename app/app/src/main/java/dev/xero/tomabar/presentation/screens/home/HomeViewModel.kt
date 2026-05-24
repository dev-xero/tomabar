package dev.xero.tomabar.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.xero.tomabar.TomaBarApplication
import dev.xero.tomabar.data.MetricsRepository
import dev.xero.tomabar.domain.models.LoadResult
import dev.xero.tomabar.domain.models.TimelineSegment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Ready(
        val sessions: List<TimelineSegment>,
        val isOffline: Boolean,
        val lastUpdated: Long?
    ) : HomeUiState
    object Empty : HomeUiState
    object NeedsReconnect : HomeUiState
}

class HomeViewModel(
    private val repo: MetricsRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = HomeUiState.Loading
        viewModelScope.launch {
            _state.value = when (val r = repo.refresh()) {
                is LoadResult.Fresh  -> HomeUiState.Ready(r.sessions, isOffline = false, lastUpdated = r.fetchedAt)
                is LoadResult.Cached -> HomeUiState.Ready(r.sessions, isOffline = true, lastUpdated = r.fetchedAt)
                LoadResult.StaleConnection -> HomeUiState.NeedsReconnect
                LoadResult.NoData    -> HomeUiState.Empty
            }
        }
    }
}

object HomeVMFactory {
    val Factory = viewModelFactory {
        initializer {
            val app = this[APPLICATION_KEY] as TomaBarApplication
            HomeViewModel(app.container.metricsRepository)
        }
    }
}