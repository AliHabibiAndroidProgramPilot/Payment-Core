package info.alihabibi.payment_core.data.local

import info.alihabibi.payment_core.data.local.dao.TransactionDao
import info.alihabibi.payment_core.domain.repository.TransactionRepository
import info.alihabibi.payment_core.domain.mapper.toDomainOrNull
import info.alihabibi.payment_core.domain.mapper.toEntity
import info.alihabibi.payment_core.domain.model.local.Transaction
import info.alihabibi.payment_core.domain.model.local.TransactionStatus
import jakarta.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun getTransactionByRequestId(requestId: String): Transaction? {
        return dao.getTransactionByRequestId(requestId).toDomainOrNull()
    }

    override suspend fun updateTransactionStatus(requestId: String, status: TransactionStatus, updatedAt: Long) {
        dao.updateTransactionStatus(requestId, status, updatedAt)
    }

    override suspend fun markTransactionSuccess(requestId: String, updatedAt: Long) {
        dao.markTransactionSuccess(requestId = requestId, updatedAt = updatedAt)
    }

    override suspend fun markTransactionFailed(requestId: String, updatedAt: Long) {
        dao.markTransactionFailed(requestId = requestId, updatedAt = updatedAt)
    }

}