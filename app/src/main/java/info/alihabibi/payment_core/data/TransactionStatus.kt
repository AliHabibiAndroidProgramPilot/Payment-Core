package info.alihabibi.payment_core.data

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