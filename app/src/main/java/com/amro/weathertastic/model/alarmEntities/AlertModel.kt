package com.amro.weathertastic.model.alarmEntities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity()
@TypeConverters(DaysTypeConverter::class)
data class AlertModel(
    @PrimaryKey(autoGenerate = true) var id: Int=0,
    val days:List<String>?,
    val alertType:String,
    val periodType:String,
    val startTime:String,
    val endTime:String,
    val maxMinValue:String,
    val thresholdValue:Double
)