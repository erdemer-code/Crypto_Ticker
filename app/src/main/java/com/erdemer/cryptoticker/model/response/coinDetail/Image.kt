package com.erdemer.cryptoticker.model.response.coinDetail


import com.google.gson.annotations.SerializedName

data class Image(
    val large: String?,
    val small: String?,
    val thumb: String?
)