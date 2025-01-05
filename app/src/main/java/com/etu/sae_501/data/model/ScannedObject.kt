package com.etu.sae_501.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_objects")
data class ScannedObject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val confidence: Float,
    val timestamp: Long,
    val description: String = "undefined", // Nouvelle colonne
    val anotherColumn: String? = null // Nouvelle colonne
)

