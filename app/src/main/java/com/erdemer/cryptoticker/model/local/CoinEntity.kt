package com.erdemer.cryptoticker.model.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "coinsTable")
@Parcelize
data class CoinEntity(
    @PrimaryKey
    val id: String,
    val name: String?,
    val symbol: String?,
    val isFavorite: Boolean = false,
    val priceChangeIntervalPosition: Int = -1,
):Parcelable {
    constructor():this("","","",false,-1)
}
