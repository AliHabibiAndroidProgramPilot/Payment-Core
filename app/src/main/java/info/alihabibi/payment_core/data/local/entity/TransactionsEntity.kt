package info.alihabibi.payment_core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import info.alihabibi.payment_core.domain.model.TransactionStatus

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["requestId"], unique = true)
    ]
)
data class TransactionsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val requestId: String,
    val amount: Long,
    val terminalId: String,
    val traceNumber: Long,
    val status: TransactionStatus, // has type converter
    val responseCode: String?,
    val rrn: String?,
    val message: String?,
    val createdAt: Long, // timestamp
    val updatedAt: Long, // timestamp
    val keepServiceAlive: Boolean
)