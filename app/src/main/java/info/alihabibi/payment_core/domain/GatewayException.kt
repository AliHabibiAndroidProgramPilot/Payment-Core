package info.alihabibi.payment_core.domain

sealed class GatewayException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class ConnectionTimeout(cause: Throwable) :
        GatewayException("Connection timeout", cause)

    class ConnectionFailed(cause: Throwable) :
            GatewayException("Couldn't reach gateway", cause)

    class ReadTimeout(cause: Throwable) :
            GatewayException("Waiting to connect timeout", cause)

    class ConnectionClosed :
            GatewayException("Gateway closed the connection without responding")

    class GatewayMalformedResponse(cause: Throwable) :
            GatewayException("Gateway returned unparsable response!", cause)

    class TransportFailed(cause: Throwable) :
            GatewayException("Unexpected I/O Failure", cause)

}