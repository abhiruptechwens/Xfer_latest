package com.ledgergreen.terminal.di

import com.ledgergreen.terminal.BuildConfig
import com.ledgergreen.terminal.data.AccountRepository
import com.ledgergreen.terminal.data.AccountRepositoryImpl
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.DefaultTokenStore
import com.ledgergreen.terminal.data.network.TokenStore
import com.ledgergreen.terminal.idle.CoroutineIdleLocker
import com.ledgergreen.terminal.idle.IdleLocker
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import com.ledgergreen.terminal.ui.common.stringresources.StringResourcesImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun accountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    abstract fun idleLocker(impl: CoroutineIdleLocker): IdleLocker

    @Binds
    abstract fun tokenStore(impl: DefaultTokenStore): TokenStore

    @Binds
    abstract fun stringResources(impl: StringResourcesImpl): StringResources
}
