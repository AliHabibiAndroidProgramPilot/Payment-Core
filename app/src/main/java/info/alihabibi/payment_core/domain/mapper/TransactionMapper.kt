package info.alihabibi.payment_core.domain.mapper

import info.alihabibi.payment_core.data.local.entity.TransactionsEntity
import info.alihabibi.payment_core.domain.model.local.Transaction

fun TransactionsEntity?.toDomainOrNull(): Transaction? {
    if (this == null) return null
     return Transaction(
        id = id,
        requestId = requestId,
        amount = amount,
        terminalId = terminalId,
        traceNumber = traceNumber,
        status = status,
        responseCode = responseCode,
        rrn = rrn,
        message = message,
        createdAt = createdAt,
        updatedAt = updatedAt,
        keepServiceAlive = keepServiceAlive
    )
}


fun Transaction.toEntity(): TransactionsEntity =
    TransactionsEntity(
        id = id,
        requestId = requestId,
        amount = amount,
        terminalId = terminalId,
        traceNumber = traceNumber,
        status = status,
        responseCode = responseCode,
        rrn = rrn,
        message = message,
        createdAt = createdAt,
        updatedAt = updatedAt,
        keepServiceAlive = keepServiceAlive
    )