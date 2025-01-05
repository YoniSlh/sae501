package com.etu.sae_501.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etu.sae_501.data.model.ScannedObject
import com.etu.sae_501.repository.ScannedObjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: ScannedObjectRepository) : ViewModel() {
    private val _history = MutableStateFlow<List<ScannedObject>>(emptyList())
    val history: StateFlow<List<ScannedObject>> = _history

    init {
        viewModelScope.launch {
            repository.getAllObjects().collect { objects ->
                _history.value = objects
            }
        }
    }

    fun deleteObject(scannedObject: ScannedObject) {
        viewModelScope.launch {
            repository.deleteObject(scannedObject)
        }
    }
}
