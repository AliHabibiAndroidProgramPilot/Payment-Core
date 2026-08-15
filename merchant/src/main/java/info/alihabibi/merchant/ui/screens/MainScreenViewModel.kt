package info.alihabibi.merchant.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import info.alihabibi.aidl_contract.PaymentRequest
import info.alihabibi.aidl_contract.PaymentResult
import info.alihabibi.merchant.payment.PaymentEvent
import info.alihabibi.merchant.repository.PaymentRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainScreenUiState> = MutableStateFlow(MainScreenUiState.Idle)
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    var requestId by mutableStateOf("")
    var amount by mutableStateOf("")
    var terminalId by mutableStateOf("")
    var traceNumber by mutableStateOf("")
    var keepServiceAlive by mutableStateOf(false)

    fun startTransaction() {
        val request = PaymentRequest(
            requestId = requestId,
            amount = amount.toLong(),
            terminalId = terminalId,
            traceNumber = traceNumber.toLong(),
            keepServiceAlive = keepServiceAlive
        )
        _uiState.value = MainScreenUiState.InProgress("Starting…")
        viewModelScope.launch {
            repository.startTransaction(request)
                .catch { e -> _uiState.value = MainScreenUiState.Error(e.message ?: "Connection error") }
                .collect { event ->
                    _uiState.value = when (event) {
                        is PaymentEvent.Started -> MainScreenUiState.InProgress("Started")
                        is PaymentEvent.Progress -> MainScreenUiState.InProgress(event.status)
                        is PaymentEvent.Completed -> MainScreenUiState.Success(event.result)
                        is PaymentEvent.Failed -> MainScreenUiState.Failed(event.result)
                    }
                }
        }
    }

    fun changeRequestId(value: String) {
        requestId = value
    }

    fun changeAmount(value: String) {
        amount = value
    }

    fun changeTerminalId(value: String) {
        terminalId = value
    }

    fun changeTraceNumber(value: String) {
        traceNumber = value
    }

    fun changeKeepServiceAlive(value: Boolean) {
        keepServiceAlive = value
    }

}

sealed interface MainScreenUiState {
    data object Idle : MainScreenUiState
    data class InProgress(val status: String) : MainScreenUiState
    data class Success(val result: PaymentResult) : MainScreenUiState
    data class Failed(val result: PaymentResult) : MainScreenUiState
    data class Error(val message: String) : MainScreenUiState
}