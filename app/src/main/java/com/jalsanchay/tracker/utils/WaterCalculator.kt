package com.jalsanchay.tracker.utils

import java.util.Locale

object WaterCalculator {
    /**
     * Formula: Roof Area (sqft) * Rainfall (mm) * 0.0929 (conversion factor) * Runoff Coefficient = Liters
     * 0.0929 is sqft to sqm conversion (1 sqft = 0.0929 sqm)
     */
    fun calculateHarvestedLiters(
        roofAreaSqFt: Double,
        rainfallMm: Double,
        runoffCoefficient: Double
    ): Double {
        return roofAreaSqFt * rainfallMm * 0.0929 * runoffCoefficient
    }

    fun formatLiters(liters: Double): String {
        return if (liters >= 1000) {
            String.format(Locale.getDefault(), "%.1f kL", liters / 1000.0)
        } else {
            String.format(Locale.getDefault(), "%.0f L", liters)
        }
    }

    fun formatWholeLiters(liters: Double): String {
        return String.format(Locale.getDefault(), "%,.0f L", liters)
    }

    fun calculateHouseholdDays(totalLiters: Double, dailyUsage: Double): Int {
        if (dailyUsage <= 0) return 0
        return (totalLiters / dailyUsage).toInt()
    }
}
