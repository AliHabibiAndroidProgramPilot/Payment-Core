package info.alihabibi.payment_core.domain.usecase

import info.alihabibi.aidl_contract.PaymentResult
import info.alihabibi.payment_core.domain.repository.TransactionRepository
import info.alihabibi.payment_core.domain.model.local.TransactionStatus
import javax.inject.Inject

class GetTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(requestId: String): PaymentResult {
        val tx = transactionRepository.getTransactionByRequestId(requestId) ?: return PaymentResult(
            requestId = requestId,
            status = TransactionStatus.CANCELLED.name,
            responseCode = "12",
            rrn = null,
            message = "Transaction not found",
            durationMs = 0
        )

        return PaymentResult(
            requestId = tx.requestId,
            status = tx.status.name,
            responseCode = tx.responseCode,
            rrn = tx.rrn,
            message = tx.message,
            durationMs = 0
        )
    }
}