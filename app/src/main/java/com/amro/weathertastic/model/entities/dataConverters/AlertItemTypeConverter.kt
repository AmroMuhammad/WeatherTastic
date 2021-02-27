package com.amro.weathertastic.model.entities.dataConverters

import androidx.room.TypeConverter
import com.amro.weathertastic.model.entities.AlertsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

class AlertItemTypeConverter {
    companion object{
        @TypeConverter
        @JvmStatic
        fun fromAlertItemList(value: MutableList<AlertsItem?>?): String? {
            val gson = Gson()
            val type = object : TypeToken<MutableList<AlertsItem>>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        @JvmStatic
        fun toAlertItemList(value: String?): MutableList<AlertsItem?>? {
            if (value == null) {
                return null
            }
            val gson = Gson()
            val type = object : TypeToken<MutableList<AlertsItem>>() {}.type
            return gson.fromJson(value, type)
        }
    }
}