package com.ledgergreen.terminal.di

import com.ledgergreen.terminal.BuildConfig
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.TokenStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun apiService(
        tokenStore: TokenStore,
    ) = ApiService(
        tokenStore = tokenStore,
        baseUrl = BuildConfig.baseUrl,
        xAccessKey = BuildConfig.xAccessKey,
    )
}
