package com.erdemer.cryptoticker.di

import com.erdemer.cryptoticker.data.remote.factory.CoinDetailFactory
import com.erdemer.cryptoticker.data.remote.factory.CoinsListFactory
import com.erdemer.cryptoticker.repository.CoinDetailRepository
import com.erdemer.cryptoticker.repository.CoinsListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideCoinsListFactory(retrofit: Retrofit) = retrofit.create(CoinsListFactory::class.java)

    @Provides
    fun provideCoinDetailFactory(retrofit: Retrofit) = retrofit.create(CoinDetailFactory::class.java)

    @Singleton
    @Provides
    fun provideCoinsListRepository(coinsListFactory: CoinsListFactory) =
        CoinsListRepository(coinsListFactory)

    @Singleton
    @Provides
    fun provideCoinDetailRepository(coinDetailFactory: CoinDetailFactory) =
        CoinDetailRepository(coinDetailFactory)
}