package com.timecapsule.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timecapsule.app.data.model.Capsule
import com.google.firebase.auth.FirebaseAuth
import com.timecapsule.app.data.model.repository.CapsuleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CapsuleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CapsuleRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _capsules = MutableStateFlow<List<Capsule>>(emptyList())
    val capsules: StateFlow<List<Capsule>> = _capsules.asStateFlow()

    init {
        loadCapsules()

        // Periodically check if any capsules should be opened
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(1000L) // Check every second
                updateCapsuleStates()
            }
        }
    }

    fun addCapsule(capsule: Capsule) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = auth.currentUser
            if (user != null) {
                try {
                    val newCapsule = capsule.copy(ownerId = user.uid)
                    repository.addCapsule(newCapsule)
                    loadCapsules() // Refresh list
                } catch (e: Exception) {
                    Log.e("CapsuleViewModel", "Failed to add capsule", e)
                }
            }
        }
    }

    fun loadCapsules() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = auth.currentUser
            if (user != null) {
                try {
                    val ownedCapsules = repository.getAllCapsulesForUser(user.uid)
                    val sharedCapsules = user.email?.let { repository.getSharedCapsules(it) } ?: emptyList()
                    
                    val combined = (ownedCapsules + sharedCapsules).distinctBy { it.id }
                    _capsules.value = combined
                } catch (e: Exception) {
                    Log.e("CapsuleViewModel", "Failed to load capsules", e)
                }
            }
        }
    }

    private suspend fun updateCapsuleStates() {
        val currentTime = System.currentTimeMillis()
        val currentList = _capsules.value
        
        // Filter out capsules that should be opened but are still marked locked locally
        val toUnlock = currentList.filter { !it.isOpened && currentTime >= it.openTime }
        
        if (toUnlock.isNotEmpty()) {
            val updatedList = currentList.map { capsule ->
                if (!capsule.isOpened && currentTime >= capsule.openTime) {
                    capsule.copy(isOpened = true)
                } else {
                    capsule
                }
            }
            
            _capsules.value = updatedList
            
            // Update Firestore in the background
            toUnlock.forEach { capsule ->
                try {
                    repository.updateCapsule(capsule.copy(isOpened = true))
                } catch (e: Exception) {
                    Log.e("CapsuleViewModel", "Failed to auto-update capsule in Firestore: ${capsule.id}", e)
                }
            }
        }
    }

    fun updateCapsuleEmotion(id: String, emotion: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentList = _capsules.value
                val capsuleToUpdate = currentList.find { it.id == id }
                if (capsuleToUpdate != null) {
                    val updated = capsuleToUpdate.copy(emotion = emotion)
                    repository.updateCapsule(updated)
                    loadCapsules()
                }
            } catch (e: Exception) {
                Log.e("CapsuleViewModel", "Failed to update capsule emotion", e)
            }
        }
    }
}
