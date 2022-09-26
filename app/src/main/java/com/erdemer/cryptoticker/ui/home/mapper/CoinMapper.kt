package com.erdemer.cryptoticker.ui.home.mapper

import com.erdemer.cryptoticker.model.local.CoinEntity
import com.erdemer.cryptoticker.model.response.CoinsListResponse


fun List<CoinsListResponse>.toCoinEntity(): List<CoinEntity> =
    this.map {
        CoinEntity(it.id ?: "", it.name, it.symbol)
    }
