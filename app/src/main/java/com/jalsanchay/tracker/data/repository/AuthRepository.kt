package com.jalsanchay.tracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jalsanchay.tracker.data.Result
import com.jalsanchay.tracker.data.model.UserProfile
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = authResult.user?.uid ?: return Result.Error(Exception("User ID null"))
            
            val snapshot = runCatching {
                firestore.collection("users").document(uid).get().await()
            }.getOrNull()
            val profile = snapshot?.toObject(UserProfile::class.java)
                ?: UserProfile(
                    uid = uid,
                    name = authResult.user?.displayName.orEmpty(),
                    email = authResult.user?.email.orEmpty()
                )

            Result.Success(profile)
        } catch (e: Exception) {
            val message = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "User not found"
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
                else -> e.localizedMessage ?: "Unknown error occurred"
            }
            Result.Error(e, message)
        }
    }

    suspend fun signUp(name: String, email: String, password: String): Result<Boolean> {
        return try {
            val trimmedName = name.trim()
            val trimmedEmail = email.trim()
            val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
            val uid = authResult.user?.uid ?: return Result.Error(Exception("User ID null"))
            authResult.user?.updateProfile(
                userProfileChangeRequest {
                    displayName = trimmedName
                }
            )
            
            val profile = UserProfile(
                uid = uid,
                name = trimmedName,
                email = trimmedEmail,
                roofAreaSqFt = 0.0,
                tankCapacityLiters = 1000.0,
                householdDailyUsageLiters = 540.0,
                runoffCoefficient = 0.8
            )
            
            firestore.collection("users").document(uid)
                .set(profile, SetOptions.merge())
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage ?: "Sign up failed")
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage ?: "Reset link failed")
        }
    }

    fun logout() {
        auth.signOut()
    }
}
