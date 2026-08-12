package info.alihabibi.aidl_contract;

import info.alihabibi.aidl_contract.model.PaymentRequest;

interface IPaymentService {

    void startTransaction(in PaymentRequest request, IPaymentCallback callback);

    PaymentResult getTransactionStatus(String requestId);

}