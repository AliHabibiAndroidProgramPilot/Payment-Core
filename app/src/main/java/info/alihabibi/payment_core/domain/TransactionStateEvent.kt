package info.alihabibi.payment_core.domain

import info.alihabibi.aidl_contract.PaymentResult
import info.alihabibi.payment_core.domain.model.local.TransactionStatus

sealed interface TransactionStateEvent {
    data class StateChanged(val state: TransactionStatus) : TransactionStateEvent
    data class Success(val result: PaymentResult) : TransactionStateEvent
    data class Failure(val result: PaymentResult) : TransactionStateEvent
}