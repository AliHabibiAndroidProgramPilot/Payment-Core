package info.alihabibi.aidl_contract;

import info.alihabibi.aidl_contract.IPaymentCallback;
import info.alihabibi.aidl_contract.PaymentRequest;
import info.alihabibi.aidl_contract.PaymentResult;

interface IPaymentService {

    void startTransaction(in PaymentRequest request, IPaymentCallback callback);

    PaymentResult getTransactionStatus(String requestId);

}