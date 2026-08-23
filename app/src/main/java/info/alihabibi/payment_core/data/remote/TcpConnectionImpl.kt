package info.alihabibi.payment_core.data.remote

import info.alihabibi.payment_core.domain.TcpConnection
import info.alihabibi.payment_core.domain.GatewayException
import info.alihabibi.payment_core.domain.model.remote.GatewayRequest
import info.alihabibi.payment_core.domain.model.remote.GatewayResponse
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketTimeoutException

class TcpConnectionImpl(private val socket: Socket) : TcpConnection {

    override suspend fun send(request: GatewayRequest) {
        try {
            val requestJSON = JSONObject().apply {
                put("requestId", request.requestId)
                put("amount", request.amount)
                put("terminalId", request.terminalId)
                put("traceNumber", request.traceNumber)
            }
            val writer = PrintWriter(socket.getOutputStream(), true)
            writer.println(requestJSON.toString())
            writer.flush()
        } catch (e: IOException) {
            throw GatewayException.TransportFailed(e)
        }
    }

    override suspend fun receive(requestId: String): GatewayResponse {
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val line = try {
            reader.readLine()
        } catch (e: SocketTimeoutException) {
            throw GatewayException.ReadTimeout(e)
        } catch (e: IOException) {
            throw GatewayException.TransportFailed(e)
        } ?: throw GatewayException.ConnectionClosed()

        return try {
            val json = JSONObject(line)
            GatewayResponse(
                requestId = json.optNullableString("requestId") ?: requestId,
                responseCode = json.optNullableString("responseCode"),
                rrn = json.optNullableString("rrn"),
                message = json.optNullableString("message")
            )
        } catch (e: JSONException) {
            throw GatewayException.GatewayMalformedResponse(e)
        }
    }

    override fun close() {
        runCatching { socket.close() }
    }

    private fun JSONObject.optNullableString(key: String): String? =
        if (!has(key) || isNull(key)) null else getString(key)

}