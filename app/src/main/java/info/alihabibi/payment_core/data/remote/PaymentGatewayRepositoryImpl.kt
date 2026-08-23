package info.alihabibi.payment_core.data.remote

import info.alihabibi.payment_core.domain.TcpConnection
import info.alihabibi.payment_core.domain.repository.PaymentGatewayRepository
import javax.inject.Inject

class PaymentGatewayRepositoryImpl @Inject constructor(
    private val tcpClient: TcpClient
) : PaymentGatewayRepository {

    override suspend fun connect(): TcpConnection = tcpClient.openConnection()

}