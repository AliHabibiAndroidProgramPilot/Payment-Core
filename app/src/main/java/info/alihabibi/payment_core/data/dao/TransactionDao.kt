package info.alihabibi.payment_core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import info.alihabibi.payment_core.data.TransactionStatus
import info.alihabibi.payment_core.data.entity.Transactions

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transactions): Long

    @Query("SELECT * FROM transactions WHERE requestId = :requestId")
    suspend fun getTransactionByRequestId(requestId: Long): Transactions?

    @Query("UPDATE transactions SET status = :status WHERE requestId = :requestId")
    suspend fun updateTransactionStatus(status: TransactionStatus, requestId: Long)

    @Query("UPDATE transactions SET status = :status WHERE requestId = :requestId")
    suspend fun markTransactionSuccess(status: TransactionStatus = TransactionStatus.SUCCESS, requestId: Long)

    @Query("UPDATE transactions SET status = :status WHERE requestId = :requestId")
    suspend fun markTransactionFailed(status: TransactionStatus = TransactionStatus.FAILED, requestId: Long)

}