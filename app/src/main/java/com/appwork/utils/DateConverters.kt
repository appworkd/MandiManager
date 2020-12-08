package com.appwork.utils

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
class DateConverters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return value?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}