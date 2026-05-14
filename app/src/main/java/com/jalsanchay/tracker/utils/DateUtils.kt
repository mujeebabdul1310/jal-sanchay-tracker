package com.jalsanchay.tracker.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
    private val monthKeyFormatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    fun getTodayString(): String = formatter.format(Date())

    fun formatDisplayDate(dateString: String): String {
        return try {
            val date = formatter.parse(dateString) ?: Date()
            displayFormatter.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    fun getMonthKey(dateString: String): String {
        return try {
            val date = formatter.parse(dateString) ?: Date()
            monthKeyFormatter.format(date)
        } catch (e: Exception) {
            "Unknown"
        }
    }
    
    fun isToday(dateString: String): Boolean = dateString == getTodayString()
}
