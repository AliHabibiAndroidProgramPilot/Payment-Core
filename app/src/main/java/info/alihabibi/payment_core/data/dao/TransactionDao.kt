package info.alihabibi.payment_core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import info.alihabibi.payment_core.domain.model.TransactionStatus
import info.alihabibi.payment_core.data.entity.TransactionsEntity

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: TransactionsEntity): Long

    @Query("SELECT * FROM transactions WHERE requestId = :requestId")
    suspend fun getTransactionByRequestId(requestId: String): TransactionsEntity?

    @Query("""
    UPDATE transactions
    SET status = :status,
        updatedAt = :updatedAt
    WHERE requestId = :requestId AND status NOT IN ('SUCCESS', 'FAILED')
""")
    suspend fun updateTransactionStatus(requestId: String, status: TransactionStatus, updatedAt: Long)

    @Query("""
    UPDATE transactions
    SET status = :status,
        updatedAt = :updatedAt
    WHERE requestId = :requestId AND status NOT IN ('FAILED')
""")
    suspend fun markTransactionSuccess(requestId: String, status: TransactionStatus = TransactionStatus.SUCCESS, updatedAt: Long)

    @Query("""
    UPDATE transactions
    SET status = :status,
        updatedAt = :updatedAt
    WHERE requestId = :requestId AND status NOT IN ('SUCCESS')
""")
    suspend fun markTransactionFailed(requestId: String, status: TransactionStatus = TransactionStatus.FAILED, updatedAt: Long)

}