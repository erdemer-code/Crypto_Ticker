package com.erdemer.cryptoticker.repository

import com.erdemer.cryptoticker.data.remote.factory.CoinDetailFactory
import com.erdemer.cryptoticker.model.response.coinDetail.CoinDetailResponse
import com.erdemer.cryptoticker.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityScoped
class CoinDetailRepository @Inject constructor(
    private val coinDetailFactory: CoinDetailFactory
) {
    suspend fun getCoinDetail(coinId: String): Resource<CoinDetailResponse> {
        val response = try {
               coinDetailFactory.getCoinDetail(coinId)
            }catch (e: Exception){
               return Resource.Error(e.message.toString())
            }
        return Resource.Success(response)
    }
}