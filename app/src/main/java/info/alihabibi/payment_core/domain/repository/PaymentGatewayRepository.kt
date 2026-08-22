package info.alihabibi.payment_core.domain.repository

import info.alihabibi.payment_core.domain.GatewayConnection

interface PaymentGatewayRepository {

    suspend fun connect(): GatewayConnection

}