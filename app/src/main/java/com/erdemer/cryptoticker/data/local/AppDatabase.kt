package com.erdemer.cryptoticker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erdemer.cryptoticker.model.local.CoinEntity

@Database(entities = [CoinEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun coinDao(): CoinDao
}