package info.alihabibi.merchant

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import info.alihabibi.merchant.payment.PaymentCoreConnector
import jakarta.inject.Inject

@HiltAndroidApp
class MerchantApp : Application(), DefaultLifecycleObserver {

    @Inject lateinit var connector: PaymentCoreConnector

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        connector.bind()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        connector.unbind()
    }

}