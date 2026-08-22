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
import info.alihabibi.merchant.sharedpref.TraceNumberPrefRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val traceNumberPref: TraceNumberPrefRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainScreenUiState> = MutableStateFlow(MainScreenUiState.Idle)
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    var requestId by mutableStateOf("")
    var amount by mutableStateOf("")
    var terminalId by mutableStateOf("")
    var traceNumber by mutableStateOf(traceNumberPref.getLatestTraceNumber().toString())
    var keepServiceAlive by mutableStateOf(false)
    var transactionStatus: PaymentResult? by mutableStateOf(null)

    fun startTransaction() {
        saveNewTraceNumber()
        transactionStatus = null
        val request = PaymentRequest(
            requestId = requestId,
            amount = amount.toLongOrNull() ?: 0,
            terminalId = terminalId,
            traceNumber = traceNumber.toLongOrNull() ?: 0,
            keepServiceAlive = keepServiceAlive
        )
        viewModelScope.launch {
            repository.startTransaction(request)
                .catch { e ->
                    _uiState.value = MainScreenUiState.Error(e.message ?: "exception happened")
                }
                .collect { event ->
                    _uiState.value = when (event) {
                        is PaymentEvent.Started -> MainScreenUiState.InProgress("Started")
                        is PaymentEvent.Progress -> MainScreenUiState.InProgress(event.status)
                        is PaymentEvent.Completed -> MainScreenUiState.Completed(event.result)
                        is PaymentEvent.Failed -> MainScreenUiState.Failed(event.result)
                    }
                }
        }
    }

    fun getTransactionStatus() {
        viewModelScope.launch {
            _uiState.update { MainScreenUiState.Idle }
            val transaction = repository.getTransactionStatus(requestId)
            transactionStatus = transaction
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

    private fun saveNewTraceNumber() {
        val newTraceNumber = this.traceNumber.toInt() + 1
        traceNumberPref.saveTraceNumber(newTraceNumber)
        this.traceNumber = newTraceNumber.toString()
    }

}

sealed interface MainScreenUiState {
    data object Idle : MainScreenUiState
    data class InProgress(val status: String) : MainScreenUiState
    data class Completed(val result: PaymentResult) : MainScreenUiState
    data class Failed(val result: PaymentResult) : MainScreenUiState
    data class Error(val message: String) : MainScreenUiState
}