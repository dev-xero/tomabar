package dev.xero.tomabar.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.xero.tomabar.TomaBarApplication
import dev.xero.tomabar.data.MetricsRepository
import dev.xero.tomabar.domain.models.ConnectResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface OnboardingUiState {
    object Idle : OnboardingUiState
    object Connecting : OnboardingUiState
    data class Error(val message: String) : OnboardingUiState
    object Connected : OnboardingUiState
}

class OnboardingViewModel(
    private val repo: MetricsRepository
) : ViewModel() {

    private val _state = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun connect(input: String) {
        _state.value = OnboardingUiState.Connecting
        viewModelScope.launch {
            _state.value = when (val r = repo.connectAndFetch(input)) {
                is ConnectResult.Success    -> OnboardingUiState.Connected
                ConnectResult.Unreachable   -> OnboardingUiState.Error(
                    "Couldn't reach that address. Check the IP and that the server is running.")
                is ConnectResult.ServerError -> OnboardingUiState.Error(
                    "The server responded with an error (${r.code}).")
                ConnectResult.InvalidAddress -> OnboardingUiState.Error(
                    "That doesn't look like a valid address.")
            }
        }
    }

    fun resetError() {
        if (_state.value is OnboardingUiState.Error) _state.value = OnboardingUiState.Idle
    }
}

object OnboardingVMFactory {
    val Factory = viewModelFactory {
        initializer {
            val app = this[APPLICATION_KEY] as TomaBarApplication
            OnboardingViewModel(app.container.metricsRepository)
        }
    }
}