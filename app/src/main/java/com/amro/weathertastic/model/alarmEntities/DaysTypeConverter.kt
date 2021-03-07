package com.amro.weathertastic.model.alarmEntities

import androidx.room.TypeConverter
import com.amro.weathertastic.model.entities.AlertsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DaysTypeConverter {
    companion object{
        @TypeConverter
        @JvmStatic
        fun fromDaysList(value: MutableList<String>): String? {
            val gson = Gson()
            val type = object : TypeToken<MutableList<String>>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        @JvmStatic
        fun toDaysList(value: String): MutableList<String> {
            val gson = Gson()
            val type = object : TypeToken<MutableList<String>>() {}.type
            return gson.fromJson(value, type)
        }
    }
}