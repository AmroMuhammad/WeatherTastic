package com.amro.weathertastic.model.alarmEntities

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface AlertDao {

    @Insert
    suspend fun insertAlarm(alert : AlertModel)
}