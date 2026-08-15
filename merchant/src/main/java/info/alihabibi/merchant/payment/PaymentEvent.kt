package info.alihabibi.merchant.payment

import info.alihabibi.aidl_contract.PaymentResult

sealed interface PaymentEvent {
    data class Started(val requestId: String) : PaymentEvent
    data class Progress(val requestId: String, val status: String) : PaymentEvent
    data class Completed(val result: PaymentResult) : PaymentEvent
    data class Failed(val result: PaymentResult) : PaymentEvent
}