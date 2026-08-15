package info.alihabibi.payment_core.domain.model

enum class TransactionStatus {

    RECEIVED,
    STORED,
    PROCESSING,
    CONNECTING,
    SENDING,
    WAITING_RESPONSE,
    SUCCESS,
    FAILED,
    CANCELLED;

}