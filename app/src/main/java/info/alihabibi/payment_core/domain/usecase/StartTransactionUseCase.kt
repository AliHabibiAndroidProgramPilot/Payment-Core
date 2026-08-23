package info.alihabibi.payment_core.domain.usecase

import info.alihabibi.aidl_contract.PaymentRequest
import info.alihabibi.aidl_contract.PaymentResult
import info.alihabibi.payment_core.domain.TransactionStateEvent
import info.alihabibi.payment_core.domain.model.local.Transaction
import info.alihabibi.payment_core.domain.model.local.TransactionStatus
import info.alihabibi.payment_core.domain.model.remote.GatewayRequest
import info.alihabibi.payment_core.domain.repository.PaymentGatewayRepository
import info.alihabibi.payment_core.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StartTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val paymentGatewayRepository: PaymentGatewayRepository
) {

    operator fun invoke(transactionRequest: PaymentRequest): Flow<TransactionStateEvent> = flow {
        val startedAt = System.currentTimeMillis()
        require(transactionRequest.requestId.isNotBlank()) { "Request id is invalid" }
        require(transactionRequest.terminalId.isNotBlank()) { "Terminal Id is invalid" }
        require(transactionRequest.amount > 0) { "Amount must be grater than 0" }
        require(transactionRequest.traceNumber > 0) { "Trace number must be grater than 0" }

        val isDuplicatedRequestId = transactionRepository.getTransactionByRequestId(transactionRequest.requestId)
        require(isDuplicatedRequestId == null) { "Request Id is duplicated!" }

        emit(TransactionStateEvent.StateChanged(TransactionStatus.RECEIVED))

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
        if (insertId < 0L) {
            emit(TransactionStateEvent.Failure(
                PaymentResult(
                    requestId = transactionRequest.requestId,
                    status = TransactionStatus.FAILED.name,
                    responseCode = "03",
                    rrn = null,
                    message = "Transaction failed to be stored",
                    durationMs = 0L
                )
            ))
            return@flow
        }

        transactionRepository.updateTransactionStatus(
            transactionRequest.requestId,
            TransactionStatus.STORED,
            System.currentTimeMillis()
        )
        emit(TransactionStateEvent.StateChanged(TransactionStatus.STORED))

        transactionRepository.updateTransactionStatus(
            transactionRequest.requestId,
            TransactionStatus.PROCESSING,
            System.currentTimeMillis()
        )
        emit(TransactionStateEvent.StateChanged(TransactionStatus.PROCESSING))

        transactionRepository.updateTransactionStatus(
            transactionRequest.requestId,
            TransactionStatus.CONNECTING,
            System.currentTimeMillis()
        )
        emit(TransactionStateEvent.StateChanged(TransactionStatus.CONNECTING))
        val tcpConnection = paymentGatewayRepository.connect()

        val tcpResponse = try {
            transactionRepository.updateTransactionStatus(
                transactionRequest.requestId,
                TransactionStatus.SENDING,
                System.currentTimeMillis()
            )
            emit(TransactionStateEvent.StateChanged(TransactionStatus.SENDING))
            tcpConnection.send(
                GatewayRequest(
                    requestId = transactionRequest.requestId,
                    amount = transactionRequest.amount,
                    terminalId = transactionRequest.terminalId,
                    traceNumber = transactionRequest.traceNumber
                )
            )

            transactionRepository.updateTransactionStatus(
                transactionRequest.requestId,
                TransactionStatus.WAITING_RESPONSE,
                System.currentTimeMillis()
            )
            emit(TransactionStateEvent.StateChanged(TransactionStatus.WAITING_RESPONSE))
            tcpConnection.receive(transactionRequest.requestId)
        } finally {
            tcpConnection.close()
        }

        val durationMs = System.currentTimeMillis() - startedAt
        if (tcpResponse.responseCode == "00") {
            transactionRepository.markTransactionSuccess(transactionRequest.requestId, System.currentTimeMillis())
            emit(TransactionStateEvent.Success(PaymentResult(
                requestId = tcpResponse.requestId,
                status = TransactionStatus.SUCCESS.name,
                responseCode = tcpResponse.responseCode,
                rrn = tcpResponse.rrn,
                message = tcpResponse.message,
                durationMs = durationMs
            ))
            )
        } else {
            transactionRepository.markTransactionFailed(transactionRequest.requestId, System.currentTimeMillis())
            emit(TransactionStateEvent.Failure(
                PaymentResult(
                    requestId = tcpResponse.requestId,
                    status = TransactionStatus.FAILED.name,
                    responseCode = tcpResponse.responseCode,
                    rrn = tcpResponse.rrn,
                    message = tcpResponse.message,
                    durationMs = durationMs
                )
            ))
        }
    }
        .catch { e ->
            emit(TransactionStateEvent.Failure(
                PaymentResult(
                    requestId = transactionRequest.requestId,
                    status = TransactionStatus.CANCELLED.name,
                    responseCode = null,
                    rrn = null,
                    message = e.message,
                    durationMs = 0
                )
            ))
        }
        .flowOn(Dispatchers.IO)

}