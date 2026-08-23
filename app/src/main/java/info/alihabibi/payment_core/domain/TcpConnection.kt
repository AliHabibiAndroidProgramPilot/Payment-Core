package info.alihabibi.payment_core.domain

import info.alihabibi.payment_core.domain.model.remote.GatewayRequest
import info.alihabibi.payment_core.domain.model.remote.GatewayResponse

interface TcpConnection {

    suspend fun send(request: GatewayRequest)

    suspend fun receive(requestId: String): GatewayResponse

    fun close()

}