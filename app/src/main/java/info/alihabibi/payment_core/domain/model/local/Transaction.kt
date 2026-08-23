package info.alihabibi.payment_core.domain.model.local

data class Transaction(
    val id: Long = 0,
    val requestId: String,
    val amount: Long,
    val terminalId: String,
    val traceNumber: Long,
    val status: TransactionStatus,
    val responseCode: String?,
    val rrn: String?,
    val message: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val keepServiceAlive: Boolean
)
