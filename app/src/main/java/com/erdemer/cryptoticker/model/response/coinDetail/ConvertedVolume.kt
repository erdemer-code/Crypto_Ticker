package com.erdemer.cryptoticker.model.response.coinDetail


import com.google.gson.annotations.SerializedName

data class ConvertedVolume(
    val btc: Double?,
    val eth: Double?,
    val usd: Double?
)