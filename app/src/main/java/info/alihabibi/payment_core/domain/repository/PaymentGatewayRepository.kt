package info.alihabibi.payment_core.domain.repository

import info.alihabibi.payment_core.domain.TcpConnection

interface PaymentGatewayRepository {

    suspend fun connect(): TcpConnection

}