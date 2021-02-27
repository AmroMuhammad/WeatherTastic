package com.amro.weathertastic.model.remoteDataSource

import com.amro.weathertastic.model.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteDataSource {
    fun getWeatherService() : WeatherAPI{
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(WeatherAPI::class.java)
    }
}