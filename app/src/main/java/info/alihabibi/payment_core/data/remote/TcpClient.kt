package info.alihabibi.payment_core.data.remote

import info.alihabibi.payment_core.domain.GatewayException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import javax.inject.Inject

class TcpClient @Inject constructor() {

    companion object {
        private const val HOST = "192.168.1.6" //Hardcoded for my real android device, change it for you test
        private const val PORT = 9999
        private const val CONNECTION_TIMEOUT = 5_000
        private const val READ_TIMEOUT = 10_000
    }

    suspend fun openConnection(): TcpConnectionImpl = withContext(Dispatchers.IO) {
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(HOST, PORT), CONNECTION_TIMEOUT)
            socket.soTimeout = READ_TIMEOUT
            TcpConnectionImpl(socket)
        } catch (e: SocketTimeoutException) {
            runCatching { socket.close() }
            throw GatewayException.ConnectionTimeout(e)
        } catch (e: IOException) {
            runCatching { socket.close() }
            throw GatewayException.ConnectionFailed(e)
        }
    }

}