package com.erdemer.cryptoticker.data.remote.factory

import com.erdemer.cryptoticker.model.response.CoinsListResponse
import retrofit2.http.GET

interface CoinsListFactory {
    @GET("coins/list")
    suspend fun getCoinsList(): List<CoinsListResponse>
}