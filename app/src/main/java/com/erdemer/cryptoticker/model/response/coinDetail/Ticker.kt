package com.erdemer.cryptoticker.model.response.coinDetail


import com.erdemer.cryptoticker.model.response.coinDetail.ConvertedLast
import com.erdemer.cryptoticker.model.response.coinDetail.ConvertedVolume
import com.google.gson.annotations.SerializedName

data class Ticker(
    val base: String?,
    @SerializedName("bid_ask_spread_percentage")
    val bidAskSpreadPercentage: Double?,
    @SerializedName("coin_id")
    val coinId: String?,
    @SerializedName("converted_last")
    val convertedLast: ConvertedLast?,
    @SerializedName("converted_volume")
    val convertedVolume: ConvertedVolume?,
    @SerializedName("is_anomaly")
    val isAnomaly: Boolean?,
    @SerializedName("is_stale")
    val isStale: Boolean?,
    val last: Double?,
    @SerializedName("last_fetch_at")
    val lastFetchAt: String?,
    @SerializedName("last_traded_at")
    val lastTradedAt: String?,
    val target: String?,
    @SerializedName("target_coin_id")
    val targetCoinId: String?,
    val timestamp: String?,
    @SerializedName("trade_url")
    val tradeUrl: String?,
    @SerializedName("trust_score")
    val trustScore: String?,
    val volume: Double?
)