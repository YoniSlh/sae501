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

    @Delete
    suspend fun deleteObject(scannedObject: ScannedObject)
}
