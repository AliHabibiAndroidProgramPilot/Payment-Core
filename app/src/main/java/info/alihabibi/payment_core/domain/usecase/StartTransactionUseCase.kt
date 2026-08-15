package info.alihabibi.payment_core.domain.usecase

import info.alihabibi.aidl_contract.PaymentRequest
import info.alihabibi.aidl_contract.PaymentResult
import info.alihabibi.payment_core.domain.TransactionRepository
import info.alihabibi.payment_core.domain.TransactionStateEvent
import info.alihabibi.payment_core.domain.model.Transaction
import info.alihabibi.payment_core.domain.model.TransactionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StartTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(transactionRequest: PaymentRequest): Flow<TransactionStateEvent> = flow {
        val startedAt = System.currentTimeMillis()
        require(transactionRequest.amount > 0) { "Amount must be grater than 0" }
        require(transactionRequest.requestId.isNotBlank()) { "Request id is invalid" }
        emit(TransactionStateEvent.StateChanged(TransactionStatus.RECEIVED))
        delay(2000)

        val insertId = transactionRepository.insertTransaction(
            Transaction(
                requestId = transactionRequest.requestId,
                amount = transactionRequest.amount,
                terminalId = transactionRequest.terminalId,
                traceNumber = transactionRequest.traceNumber,
                status = TransactionStatus.RECEIVED,
                createdAt = startedAt,
                responseCode = null,
                rrn = null,
                message = null,
                updatedAt = startedAt,
                keepServiceAlive = transactionRequest.keepServiceAlive
            )
        )
        if (insertId > 0) {
            transactionRepository.updateTransactionStatus(
                transactionRequest.requestId,
                TransactionStatus.STORED,
                System.currentTimeMillis()
            )
            emit(TransactionStateEvent.StateChanged(TransactionStatus.STORED))
            delay(2000)
        }
        transactionRepository.updateTransactionStatus(
            transactionRequest.requestId,
            TransactionStatus.PROCESSING,
            System.currentTimeMillis()
        )
        emit(TransactionStateEvent.StateChanged(TransactionStatus.PROCESSING))
        delay(2000)

        transactionRepository.updateTransactionStatus(
            transactionRequest.requestId,
            TransactionStatus.CONNECTING,
            System.currentTimeMillis()
        )
        emit(TransactionStateEvent.StateChanged(TransactionStatus.CONNECTING))
        delay(2000)
        //tcp client connect

        transactionRepository.updateTransactionStatus(
            transactionRequest.requestId,
            TransactionStatus.SENDING,
            System.currentTimeMillis()
        )
        emit(TransactionStateEvent.StateChanged(TransactionStatus.SENDING))
        delay(2000)
        //tcp client send

        transactionRepository.updateTransactionStatus(
            transactionRequest.requestId,
            TransactionStatus.WAITING_RESPONSE,
            System.currentTimeMillis()
        )
        emit(TransactionStateEvent.StateChanged(TransactionStatus.WAITING_RESPONSE))
        delay(2000)
        //tcp client get

        val durationMs = System.currentTimeMillis() - startedAt

        //if(tcpClient.responseCode == TCPResponseCodes.SUCCESS)
        transactionRepository.markTransactionSuccess(transactionRequest.requestId, System.currentTimeMillis())
        emit(TransactionStateEvent.Success(
            PaymentResult(
                requestId = transactionRequest.requestId,
                status = TransactionStatus.SUCCESS.name,
                responseCode = "00",
                rrn = null,
                message = "null",
                durationMs = durationMs
            )
        ))
        delay(2000)
        // else
        transactionRepository.markTransactionFailed(transactionRequest.requestId, System.currentTimeMillis())
        emit(TransactionStateEvent.Failure(
            PaymentResult(
                requestId = transactionRequest.requestId,
                status = TransactionStatus.FAILED.name,
                responseCode = "12",
                rrn = null,
                message = null,
                durationMs = durationMs
            )
        ))
    }
        .catch { e ->
            transactionRepository.markTransactionFailed(transactionRequest.requestId, System.currentTimeMillis())
            emit(TransactionStateEvent.Failure(
                PaymentResult(
                    requestId = transactionRequest.requestId,
                    status = TransactionStatus.FAILED.name,
                    responseCode = "12",
                    rrn = null,
                    message = e.message,
                    durationMs = 0
                )
            ))
        }
        .flowOn(Dispatchers.IO)

}