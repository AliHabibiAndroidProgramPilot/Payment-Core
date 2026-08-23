package info.alihabibi.payment_core.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import info.alihabibi.payment_core.data.local.TransactionRepositoryImpl
import info.alihabibi.payment_core.data.remote.PaymentGatewayRepositoryImpl
import info.alihabibi.payment_core.domain.repository.PaymentGatewayRepository
import info.alihabibi.payment_core.domain.repository.TransactionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindPaymentGatewayRepository(impl: PaymentGatewayRepositoryImpl): PaymentGatewayRepository

}