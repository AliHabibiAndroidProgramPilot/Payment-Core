package info.alihabibi.payment_core.domain.model.remote

data class GatewayResponse(
    val requestId: String,
    val responseCode: String?,
    val rrn: String?,
    val message: String?
)
