package com.erdemer.cryptoticker.ui.coinDetail.model

data class CoinDetailUiModel(
    val id: String?,
    val name: String?,
    val symbol: String?,
    val hashingAlgorithm: String?,
    val imageUrl: String?,
    val description: String?,
    val currentPriceUSD: Double?,
    val currentPriceEUR: Double?,
    val currentPriceTRY: Double?,
    val generalPriceChangePercent24h: Double?,
    val priceChangePercent24hUSD: Double?,
    val priceChangePercent24hEUR: Double?,
    val priceChangePercent24hTRY: Double?

)
