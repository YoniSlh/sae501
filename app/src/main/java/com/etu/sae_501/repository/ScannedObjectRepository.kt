package com.etu.sae_501.repository

import com.etu.sae_501.data.database.ScannedObjectDao
import com.etu.sae_501.data.model.ScannedObject
import kotlinx.coroutines.flow.Flow

class ScannedObjectRepository(private val dao: ScannedObjectDao) {

    fun getAllObjects(): Flow<List<ScannedObject>> {
        return dao.getAllObjects()
    }

    suspend fun insertObject(scannedObject: ScannedObject) {
        dao.insertObject(scannedObject)
    }

    suspend fun deleteObject(scannedObject: ScannedObject) {
        dao.deleteObject(scannedObject)
    }

    suspend fun updateObject(scannedObject: ScannedObject) {
        dao.updateObject(scannedObject)
    }

    suspend fun getScannedObjectById(id: Long): ScannedObject? {
        return dao.getScannedObjectById(id)
    }

    suspend fun updateFavoriteStatus(id: Long, newState: Boolean) {
        return dao.updateFavoriteStatus(id, newState);
    }

    fun getFavoriteItems(): Flow<List<ScannedObject>> {
        return dao.getFavoriteItems()
    }
}
