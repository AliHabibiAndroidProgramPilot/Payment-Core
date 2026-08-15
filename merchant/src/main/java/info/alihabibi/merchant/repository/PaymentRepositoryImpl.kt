package info.alihabibi.merchant.repository

import info.alihabibi.aidl_contract.PaymentRequest
import info.alihabibi.aidl_contract.PaymentResult
import info.alihabibi.merchant.payment.PaymentCoreConnector
import info.alihabibi.merchant.payment.PaymentEvent
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class PaymentRepositoryImpl @Inject constructor(
    private val connector: PaymentCoreConnector
) : PaymentRepository {

    override fun startTransaction(request: PaymentRequest): Flow<PaymentEvent> =
        connector.startTransaction(request)

    override suspend fun getTransactionStatus(requestId: String): PaymentResult? =
        connector.getTransactionStatus(requestId)

}