package com.etu.sae_501.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return instance!!
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Créer une nouvelle table temporaire
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS scanned_objects_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                confidence REAL NOT NULL DEFAULT 0.0,
                timestamp INTEGER NOT NULL,
                description TEXT NOT NULL DEFAULT 'undefined',
                another_column TEXT DEFAULT NULL
            )
            """.trimIndent()
        )

        // Copier les données existantes
        database.execSQL(
            """
            INSERT INTO scanned_objects_new (id, name, confidence, timestamp)
            SELECT id, name, confidence, timestamp FROM scanned_objects
            """.trimIndent()
        )

        // Supprimer l'ancienne table
        database.execSQL("DROP TABLE scanned_objects")

        // Renommer la nouvelle table
        database.execSQL("ALTER TABLE scanned_objects_new RENAME TO scanned_objects")
    }
}



