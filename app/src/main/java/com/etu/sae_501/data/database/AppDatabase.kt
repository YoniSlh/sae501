package com.etu.sae_501.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.etu.sae_501.data.model.ScannedObject

@Database(entities = [ScannedObject::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scannedObjectDao(): ScannedObjectDao
}
