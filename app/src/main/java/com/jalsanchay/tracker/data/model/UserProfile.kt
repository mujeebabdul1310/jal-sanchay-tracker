package com.jalsanchay.tracker.data.model

import com.google.firebase.Timestamp

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val roofAreaSqFt: Double = 0.0,
    val tankCapacityLiters: Double = 1000.0,
    val householdDailyUsageLiters: Double = 540.0,
    val runoffCoefficient: Double = 0.8,
    val createdAt: Timestamp = Timestamp.now()
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", 0.0, 1000.0, 540.0, 0.8, Timestamp.now())
}
