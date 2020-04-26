package com.plugin.justiceapp.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plugin.justiceapp.models.LocalOrder

@Database(entities = [LocalOrder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localOrderDao(): LocalOrderDao
}