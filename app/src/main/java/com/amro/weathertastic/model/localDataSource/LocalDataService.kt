package com.amro.weathertastic.model.localDataSource

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase

object LocalDataService {
    fun getInstance(application: Application) : WeatherDao{
        return Room.databaseBuilder(application, WeatherDatabase::class.java, "WeatherDatabase").build().weatherDao()
    }
}