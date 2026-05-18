package com.timecapsule.app.data.model.repository

import com.timecapsule.app.data.model.Capsule

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CapsuleRepository {

    private val db = FirebaseFirestore.getInstance()
    private val capsulesCollection = db.collection("Capsules")

    suspend fun addCapsule(capsule: Capsule) {
        val documentRef = if (capsule.id.isEmpty()) {
            capsulesCollection.document()
        } else {
            capsulesCollection.document(capsule.id)
        }
        val newCapsule = capsule.copy(id = documentRef.id)
        documentRef.set(newCapsule).await()
    }

    suspend fun getAllCapsulesForUser(userId: String): List<Capsule> {
        val snapshot = capsulesCollection.whereEqualTo("ownerId", userId).get().await()
        return snapshot.toObjects(Capsule::class.java)
    }

    suspend fun getSharedCapsules(email: String): List<Capsule> {
        val snapshot = capsulesCollection.whereArrayContains("sharedUsers", email).get().await()
        return snapshot.toObjects(Capsule::class.java)
    }

    suspend fun updateCapsule(capsule: Capsule) {
        if (capsule.id.isNotEmpty()) {
            capsulesCollection.document(capsule.id).set(capsule).await()
        }
    }
}