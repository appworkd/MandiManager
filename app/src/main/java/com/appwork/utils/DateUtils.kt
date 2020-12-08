package com.appwork.utils

import java.text.DateFormat
import java.util.*

object DateUtils {
    //Get current date string
    val currentDate: String
        get() {
            val calendar = Calendar.getInstance()
            val today = calendar.time
            return DateFormat.getDateInstance().format(today)
        }

    //Get current millis
    val currentDateInMillis: Long
        get() {
            val calendar = Calendar.getInstance()
            return calendar.timeInMillis
        }

    fun getStringCurrentDate(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val today = calendar.time
        return DateFormat.getDateInstance().format(today)
    }
}