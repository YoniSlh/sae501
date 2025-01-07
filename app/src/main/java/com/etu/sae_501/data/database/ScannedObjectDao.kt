package com.etu.sae_501.data.database

import androidx.room.*
import com.etu.sae_501.data.model.ScannedObject
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedObjectDao {
    @Query("SELECT * FROM scanned_objects ORDER BY timestamp DESC")
    fun getAllObjects(): Flow<List<ScannedObject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObject(scannedObject: ScannedObject)

    @Update
    suspend fun updateObject(scannedObject: ScannedObject)

    @Delete
    suspend fun deleteObject(scannedObject: ScannedObject)

    @Query("SELECT * FROM scanned_objects WHERE id = :id LIMIT 1")
    suspend fun getScannedObjectById(id: Long): ScannedObject?

    @Query("UPDATE scanned_objects SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM scanned_objects WHERE isFavorite = 1")
    fun getFavoriteItems(): Flow<List<ScannedObject>>

}
