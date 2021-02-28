package com.amro.weathertastic.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.model.localDataSource.LocalDataSource
import com.amro.weathertastic.model.remoteDataSource.*
import com.amro.weathertastic.utils.Constants
import kotlinx.coroutines.*

class WeatherRepository(private val application: Application) {
    private val remoteDataSource = RemoteDataSource
    private val localDataSource = LocalDataSource.getInstance(application)

    fun loadCurrentData():LiveData<WeatherResponse>{
        val lat = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LATITUDE,"0").toString()
        val long = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LONGITUDE,"0").toString()
        val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            Log.i(Constants.LOG_TAG,"exception from retrofit")
        }
        runBlocking {
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            val response = remoteDataSource.getWeatherService().getAllData(lat, long, Constants.EXCLUDE_MINUTELY, "default", "en", Constants.WEATHER_API_KEY)
            if(response.isSuccessful){
                localDataSource.insertDefault(response.body())
                Log.i(Constants.LOG_TAG,"success")
            }
        }
        }
        return localDataSource.getDefault(lat,long)
    }

}