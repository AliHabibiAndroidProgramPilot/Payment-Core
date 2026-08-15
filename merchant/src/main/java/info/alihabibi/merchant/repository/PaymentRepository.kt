package info.alihabibi.merchant.repository

import info.alihabibi.aidl_contract.PaymentRequest
import info.alihabibi.aidl_contract.PaymentResult
import info.alihabibi.merchant.payment.PaymentEvent
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    fun startTransaction(request: PaymentRequest): Flow<PaymentEvent>

    suspend fun getTransactionStatus(requestId: String): PaymentResult?

}