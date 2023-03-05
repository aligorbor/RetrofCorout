package com.example.retrofcorout.data.database

import androidx.room.TypeConverter
import java.util.*

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }
    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? {
        return if (date == null) null else date.getTime()
    }
}