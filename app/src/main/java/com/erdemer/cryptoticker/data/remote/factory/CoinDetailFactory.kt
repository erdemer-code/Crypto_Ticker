package com.erdemer.cryptoticker.data.remote.factory

import com.erdemer.cryptoticker.model.response.coinDetail.CoinDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinDetailFactory {
    @GET("coins/{coinId}")
    suspend fun getCoinDetail(@Path("coinId") coinId: String): CoinDetailResponse
}