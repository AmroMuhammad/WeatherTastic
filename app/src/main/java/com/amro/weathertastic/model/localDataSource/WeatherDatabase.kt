package com.amro.weathertastic.model.localDataSource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amro.weathertastic.model.entities.WeatherResponse

@Database(entities = arrayOf(WeatherResponse::class), version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun weatherDao(): WeatherDao

}