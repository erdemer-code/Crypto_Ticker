package com.erdemer.cryptoticker.repository

import com.erdemer.cryptoticker.data.remote.factory.CoinsListFactory
import com.erdemer.cryptoticker.model.response.CoinsListResponse
import com.erdemer.cryptoticker.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class CoinsListRepository @Inject constructor(private val coinsListFactory: CoinsListFactory) {
    suspend fun getCoinsList(): Resource<List<CoinsListResponse>> {
        val response = try {
            coinsListFactory.getCoinsList()
        } catch (e: Exception){
            return Resource.Error(e.message ?: "An error occurred")
        }
        return Resource.Success(response)
    }
}