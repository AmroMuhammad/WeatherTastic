package com.amro.weathertastic.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.model.localDataSource.LocalDataSource
import com.amro.weathertastic.model.remoteDataSource.RemoteDataSource
import com.amro.weathertastic.utils.Constants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherRepository(application: Application) {
    private val remoteDataSource = RemoteDataSource
    private val localDataSource = LocalDataSource.getInstance(application)
    private val lat = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LATITUDE,"null").toString()
    private val long = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LONGITUDE,"null").toString()
    private val oldLat = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.OLD_LATITUDE,"null").toString()
    private val oldLong = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.OLD_LONGITUDE,"null").toString()


    fun loadCurrentData():LiveData<WeatherResponse>{
        val exceptionHandlerException = CoroutineExceptionHandler { _, t:Throwable ->
            Log.i(Constants.LOG_TAG,t.message.toString())
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            if (lat != null) {
                val response = remoteDataSource.getWeatherService().getAllData(lat, long, Constants.EXCLUDE_MINUTELY, "default", "en", Constants.WEATHER_API_KEY)
                if (response.isSuccessful) {
                    localDataSource.insertDefault(response.body())
                    localDataSource.deleteDefault(oldLat,oldLong)
                    Log.i(Constants.LOG_TAG, "success")
                }
            }
        }
        Log.i(Constants.LOG_TAG, "outhere")
        return localDataSource.getDefault(lat,long)
    }

    fun fetchFavouriteList(latitude: String, longitude: String): LiveData<List<WeatherResponse>> {
        val exceptionHandlerException = CoroutineExceptionHandler { _, t:Throwable ->
            Log.i(Constants.LOG_TAG,t.message.toString()) }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            if(latitude!="0" && longitude!="0"){
                val response = remoteDataSource.getWeatherService().getAllData(latitude, longitude, Constants.EXCLUDE_MINUTELY, "default", "en", Constants.WEATHER_API_KEY)
                if (response.isSuccessful) {
                    localDataSource.insertDefault(response.body())
                    Log.i(Constants.LOG_TAG, "success fav")
                }
            }
        }
        Log.i(Constants.LOG_TAG, "outhere fav")
        return localDataSource.getFavList(lat,long)
    }

}