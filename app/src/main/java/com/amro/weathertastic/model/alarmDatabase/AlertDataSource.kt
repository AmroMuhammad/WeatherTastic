package com.amro.weathertastic.model.alarmDatabase

import android.app.Application
import androidx.room.Room

object AlertDataSource {
    fun getInstance(application: Application) : AlertDao {
        return Room.databaseBuilder(application, AlertDatabase::class.java, "alertDatabase").build().alertDao()
    }
}