package com.erdemer.cryptoticker.model.response.coinDetail


import com.google.gson.annotations.SerializedName

data class CoinDetailResponse(

    @SerializedName("asset_platform_id")
    val assetPlatformId: String?,
    @SerializedName("block_time_in_minutes")
    val blockTimeInMinutes: Int?,
    @SerializedName("categories")
    val categories: List<String>?,
    @SerializedName("contract_address")
    val contractAddress: String?,
    @SerializedName("country_origin")
    val countryOrigin: String?,
    @SerializedName("description")
    val description: Description?,
    @SerializedName("hashing_algorithm")
    val hashingAlgorithm: String?,
    val id: String?,
    val image: Image?,
    @SerializedName("last_updated")
    val lastUpdated: String?,
    @SerializedName("market_data")
    val marketData: MarketData?,
    val name: String?,
    val symbol: String?,
    val tickers: List<Ticker>?
)