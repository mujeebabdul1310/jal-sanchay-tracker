package com.jalsanchay.tracker.data.model

data class RainfallEntry(
    val id: String = "",
    val date: String = "", // yyyy-MM-dd
    val rainfallMm: Double = 0.0,
    val litersHarvested: Double = 0.0,
    val roofAreaSqFt: Double = 0.0,
    val runoffCoefficient: Double = 0.8,
    val timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", 0.0, 0.0, 0.0, 0.8, System.currentTimeMillis())
}
