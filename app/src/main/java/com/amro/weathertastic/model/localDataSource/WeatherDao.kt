package com.amro.weathertastic.model.localDataSource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amro.weathertastic.model.entities.WeatherItem
import com.amro.weathertastic.model.entities.WeatherResponse

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefault(weatherResponse: WeatherResponse?)

    @Query("SELECT * from WeatherResponse WHERE lon = :lon")
    fun getDefault(lon:Double):LiveData<WeatherResponse>

}