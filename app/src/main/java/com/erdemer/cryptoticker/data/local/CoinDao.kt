package com.erdemer.cryptoticker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.erdemer.cryptoticker.model.local.CoinEntity

@Dao
interface CoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg coins: CoinEntity): List<Long>

    @Query("SELECT * FROM coinsTable")
    suspend fun getAllCoins(): List<CoinEntity>

    @Query("SELECT* FROM coinsTable WHERE name LIKE '%' || :searchStr || '%' OR symbol LIKE '%' || :searchStr || '%' ")
    suspend fun searchCoins(searchStr: String): List<CoinEntity>

    @Query("SELECT* FROM coinsTable WHERE id = :id")
    suspend fun getCoin(id: String): CoinEntity

    @Query("SELECT* FROM coinsTable WHERE isFavorite = 1")
    suspend fun getFavoriteCoins(): List<CoinEntity>

    @Update(onConflict = REPLACE)
    suspend fun updateCoin(coin: CoinEntity)


}
