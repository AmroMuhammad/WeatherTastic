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
import com.google.gson.Gson
import kotlinx.coroutines.*

class WeatherRepository(private val application: Application) {
    private val remoteDataSource = RemoteDataSource
    private val localDataSource = LocalDataSource.getInstance(application)
    private val localCurrentLiveData= MutableLiveData<WeatherResponse>()

    fun loadCurrentData():LiveData<WeatherResponse>{
        val lat = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LATITUDE,"null").toString()
        val long = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LONGITUDE,"null").toString()
        val oldLat = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.OLD_LATITUDE,"null").toString()
        val oldLong = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.OLD_LONGITUDE,"null").toString()
        val exceptionHandlerException = CoroutineExceptionHandler { _, t:Throwable ->
            Log.i(Constants.LOG_TAG,t.message.toString())
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            if (lat != null) {
                val response = remoteDataSource.getWeatherService().getAllData(lat, long, Constants.EXCLUDE_MINUTELY, "default", "en", Constants.WEATHER_API_KEY)
                if (response.isSuccessful) {
                    localDataSource.insertDefault(response.body())
                    localDataSource.deleteDefault(oldLat,oldLong)
//                    val currentLocation = Gson().toJson(response.body())
//                    application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION,Context.MODE_PRIVATE).edit().putString(Constants.CURRENT_LOCATION,currentLocation).apply()
//                    localCurrentLiveData.postValue(response.body())
                    Log.i(Constants.LOG_TAG, "success")
                }
            }
        }
        Log.i(Constants.LOG_TAG, "outhere")
//        val obo = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LOCATION,"{}")
//        val weatherObject = Gson().fromJson(obo.toString(),WeatherResponse::class.java)
//        localCurrentLiveData.postValue(weatherObject)
//        return  localCurrentLiveData
        return localDataSource.getDefault(lat,long)
    }

}