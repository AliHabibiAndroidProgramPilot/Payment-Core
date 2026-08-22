package info.alihabibi.payment_core.domain.usecase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import info.alihabibi.payment_core.domain.repository.PaymentGatewayRepository
import info.alihabibi.payment_core.domain.repository.TransactionRepository

@Module
@InstallIn(ServiceComponent::class)
object UseCasesModule {

    @Provides
    @ServiceScoped
    fun provideStartTransactionUseCase(
        transactionRepository: TransactionRepository,
        paymentGatewayRepository: PaymentGatewayRepository
    ): StartTransactionUseCase = StartTransactionUseCase(transactionRepository, paymentGatewayRepository)

    @Provides
    @ServiceScoped
    fun provideGetTransactionUseCase(
        transactionRepository: TransactionRepository
    ): GetTransactionUseCase = GetTransactionUseCase(transactionRepository)

}