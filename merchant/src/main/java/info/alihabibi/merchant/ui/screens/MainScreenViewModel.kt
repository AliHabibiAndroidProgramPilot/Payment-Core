package info.alihabibi.merchant.ui.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainScreenViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<MainScreenUiState> = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    fun startTransaction() {

    }

    fun changeValues(event: UiEvent) {
        when(event) {
            is UiEvent.OnChangeRequestId -> { _uiState.update { it.copy(requestId = event.value) } }
            is UiEvent.OnChangeAmount -> { _uiState.update { it.copy(amount = event.value) } }
            is UiEvent.OnChangeTerminalId -> { _uiState.update { it.copy(terminalId = event.value) } }
            is UiEvent.OnChangeTraceNumber -> { _uiState.update { it.copy(traceNumber = event.value) } }
            is UiEvent.OnChangeKeepServiceAlive -> { _uiState.update { it.copy(keepServiceAlive = event.value) } }
        }
    }

}

sealed interface UiEvent {
    data class OnChangeRequestId(val value: String) : UiEvent
    data class OnChangeAmount(val value: String) : UiEvent
    data class OnChangeTerminalId(val value: String) : UiEvent
    data class OnChangeTraceNumber(val value: String) : UiEvent
    data class OnChangeKeepServiceAlive(val value: Boolean) : UiEvent
}

@Immutable
data class MainScreenUiState(
    val requestId: String = "",
    val amount: String = "200000",
    val terminalId: String = "",
    val traceNumber: String = "0",
    val keepServiceAlive: Boolean = true
)