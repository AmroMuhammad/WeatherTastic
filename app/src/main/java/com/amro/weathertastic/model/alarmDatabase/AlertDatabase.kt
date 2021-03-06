package com.amro.weathertastic.model.alarmDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AlertModel::class], version = 1, exportSchema = false)
abstract class AlertDatabase :RoomDatabase(){
    abstract fun alertDao(): AlertDao
}