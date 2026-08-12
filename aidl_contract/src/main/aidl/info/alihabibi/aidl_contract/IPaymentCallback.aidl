package info.alihabibi.aidl_contract;

import info.alihabibi.aidl_contract.model.PaymentResult;

interface IPaymentCallback {

    void onTransactionStarted(String requestId);

    void onTransactionProgress(String requestId, String status);

    void onTransactionComplete(in PaymentResult result);

    void onTransactionFailed(in PaymentResult result);

}