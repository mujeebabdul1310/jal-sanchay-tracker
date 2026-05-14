package com.jalsanchay.tracker.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.jalsanchay.tracker.data.Result
import com.jalsanchay.tracker.data.model.RainfallEntry
import com.jalsanchay.tracker.data.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RainfallRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getUserProfile(uid: String): Flow<Result<UserProfile>> = callbackFlow {
        trySend(Result.Loading)
        val subscription = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    if (profile != null) trySend(Result.Success(profile))
                } else if (snapshot != null) {
                    trySend(Result.Success(UserProfile(uid = uid)))
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): Result<Boolean> {
        return try {
            firestore.collection("users").document(uid)
                .set(updates, SetOptions.merge())
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getRainfallEntries(uid: String): Flow<Result<List<RainfallEntry>>> = callbackFlow {
        trySend(Result.Loading)
        val subscription = firestore.collection("users").document(uid)
            .collection("rainfall_entries")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val entries = snapshot.documents.mapNotNull { it.toObject(RainfallEntry::class.java)?.copy(id = it.id) }
                    trySend(Result.Success(entries))
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addRainfallEntry(uid: String, entry: RainfallEntry): Result<Boolean> {
        return try {
            val collection = firestore.collection("users").document(uid)
                .collection("rainfall_entries")
            val document = if (entry.id.isBlank()) collection.document() else collection.document(entry.id)

            document.set(entry.copy(id = document.id))
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteRainfallEntry(uid: String, entryId: String): Result<Boolean> {
        return try {
            firestore.collection("users").document(uid)
                .collection("rainfall_entries")
                .document(entryId).delete().await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
