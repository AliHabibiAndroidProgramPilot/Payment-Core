package info.alihabibi.payment_core.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import info.alihabibi.payment_core.data.TransactionStatus

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["requestId"], unique = true)
    ]
)
data class Transactions(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val requestId: Long,
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