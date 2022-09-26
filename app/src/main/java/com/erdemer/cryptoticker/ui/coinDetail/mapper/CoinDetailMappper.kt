package com.erdemer.cryptoticker.ui.coinDetail.mapper

import com.erdemer.cryptoticker.model.response.coinDetail.CoinDetailResponse
import com.erdemer.cryptoticker.ui.coinDetail.model.CoinDetailUiModel


fun CoinDetailResponse.toCoinDetailUiModel(): CoinDetailUiModel =
    CoinDetailUiModel(
        id = id,
        name = name,
        symbol = symbol,
        hashingAlgorithm = hashingAlgorithm,
        imageUrl = image?.large,
        description = if (description?.en.isNullOrEmpty().not())description?.en else null,
        currentPriceUSD = marketData?.currentPrice?.usd,
        currentPriceEUR = marketData?.currentPrice?.eur,
        currentPriceTRY = marketData?.currentPrice?.tryX,
        generalPriceChangePercent24h = marketData?.priceChangePercentage24h,
        priceChangePercent24hUSD = marketData?.priceChangePercentage24hInCurrency?.usd,
        priceChangePercent24hEUR = marketData?.priceChangePercentage24hInCurrency?.eur,
        priceChangePercent24hTRY = marketData?.priceChangePercentage24hInCurrency?.tryX,
    )