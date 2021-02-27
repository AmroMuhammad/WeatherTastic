package com.amro.weathertastic.model.entities

import androidx.room.*
import com.amro.weathertastic.model.entities.dataConverters.AlertItemTypeConverter
import com.amro.weathertastic.model.entities.dataConverters.DailyItemTypeConverter
import com.amro.weathertastic.model.entities.dataConverters.HourlyItemTypeConverter
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["lon", "lat"])
@JvmSuppressWildcards
@TypeConverters(AlertItemTypeConverter::class,DailyItemTypeConverter::class,HourlyItemTypeConverter::class)
data class WeatherResponse(

	@field:SerializedName("alerts")
	val alerts: List<AlertsItem?>? = null,

	@field:SerializedName("current")
	@Embedded(prefix = "current_")
	val current: Current? = null,

	@field:SerializedName("timezone")
	val timezone: String? = null,

	@field:SerializedName("timezone_offset")
	val timezoneOffset: Int? = null,

	@field:SerializedName("daily")
	val daily: List<DailyItem?>? = null,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("lat")
	val lat: Double,

	@field:SerializedName("hourly")
	val hourly: List<HourlyItem?>? = null

)