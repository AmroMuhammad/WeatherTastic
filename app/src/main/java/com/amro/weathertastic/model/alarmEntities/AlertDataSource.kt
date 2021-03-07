package com.amro.weathertastic.model.alarmEntities

import android.app.Application
import androidx.room.Room
import com.amro.weathertastic.model.localDataSource.WeatherDao
import com.amro.weathertastic.model.localDataSource.WeatherDatabase

object AlertDataSource {
    fun getInstance(application: Application) : AlertDao {
        return Room.databaseBuilder(application, AlertDatabase::class.java, "alertDatabase").build().alertDao()
    }
}