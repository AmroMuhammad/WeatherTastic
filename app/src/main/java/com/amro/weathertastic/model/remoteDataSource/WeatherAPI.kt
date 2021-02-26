package com.amro.weathertastic.model.remoteDataSource

import com.amro.weathertastic.model.entities.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("onecall")
    suspend fun getAllData(@Query("lat") lat:String,@Query("lon") lon:String,@Query("exclude") exclude:String,
                   @Query("units") units:String,@Query("lang") lang:String,@Query("appid") appid:String) : Response<WeatherResponse>
}