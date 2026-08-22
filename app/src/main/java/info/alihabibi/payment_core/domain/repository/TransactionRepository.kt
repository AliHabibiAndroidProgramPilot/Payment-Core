package info.alihabibi.payment_core.domain.repository

import info.alihabibi.payment_core.domain.model.local.Transaction
import info.alihabibi.payment_core.domain.model.local.TransactionStatus

interface TransactionRepository {

    suspend fun insertTransaction(transaction: Transaction): Long

    suspend fun getTransactionByRequestId(requestId: String): Transaction?

    suspend fun updateTransactionStatus(requestId: String, status: TransactionStatus, updatedAt: Long)

    suspend fun markTransactionSuccess(requestId: String, updatedAt: Long)

    suspend fun markTransactionFailed(requestId: String, updatedAt: Long)

}