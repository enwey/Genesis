package com.genesis.app.di

import com.genesis.app.data.repository.BazaarRepositoryImpl
import com.genesis.app.data.repository.CreationRepositoryImpl
import com.genesis.app.data.repository.SocialRepositoryImpl
import com.genesis.app.data.repository.WalletRepositoryImpl
import com.genesis.app.domain.repository.AuthRepository
import com.genesis.app.domain.repository.BazaarRepository
import com.genesis.app.domain.repository.CreationRepository
import com.genesis.app.domain.repository.SocialRepository
import com.genesis.app.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCreationRepository(
        creationRepositoryImpl: CreationRepositoryImpl
    ): CreationRepository

    @Binds
    @Singleton
    abstract fun bindBazaarRepository(
        bazaarRepositoryImpl: BazaarRepositoryImpl
    ): BazaarRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: WalletRepositoryImpl
    ): WalletRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(
        socialRepositoryImpl: SocialRepositoryImpl
    ): SocialRepository
}
