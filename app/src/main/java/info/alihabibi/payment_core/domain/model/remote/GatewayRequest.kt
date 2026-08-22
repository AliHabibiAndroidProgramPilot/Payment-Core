package info.alihabibi.payment_core.domain.model.remote

data class GatewayRequest(
    val requestId: String,
    val amount: Long,
    val terminalId: String,
    val traceNumber: Long
)
