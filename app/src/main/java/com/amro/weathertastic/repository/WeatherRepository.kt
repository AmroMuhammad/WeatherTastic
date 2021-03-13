package com.amro.weathertastic.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.amro.weathertastic.model.alarmDatabase.AlertDataSource
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.model.localDataSource.LocalDataSource
import com.amro.weathertastic.model.remoteDataSource.RemoteDataSource
import com.amro.weathertastic.utils.Constants
import kotlinx.coroutines.*

class WeatherRepository(application: Application) {
    private val remoteDataSource = RemoteDataSource
    private val localDataSource = LocalDataSource.getInstance(application)
    private val localAlarmDatabase = AlertDataSource.getInstance(application)

    private val lat = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LATITUDE,"null").toString()
    private val long = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LONGITUDE,"null").toString()
    private val oldLat = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.OLD_LATITUDE,"null").toString()
    private val oldLong = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.OLD_LONGITUDE,"null").toString()
    private val language = application.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.LANGUAGE,"en").toString()
    private val units = application.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.UNITS,"metric").toString()



    fun loadAllData():LiveData<List<WeatherResponse>>{
        val exceptionHandlerException = CoroutineExceptionHandler { _, t:Throwable ->
            Log.i(Constants.LOG_TAG,t.message.toString())
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            if (lat != null) {
                val response = remoteDataSource.getWeatherService().getAllData(lat, long, Constants.EXCLUDE_MINUTELY, units, language, Constants.WEATHER_API_KEY)
                if (response.isSuccessful) {
                    localDataSource.insertDefault(response.body())
                    localDataSource.deleteDefault(oldLat,oldLong)
                    Log.i(Constants.LOG_TAG, "success")
                }
            }
        }
        Log.i(Constants.LOG_TAG, "outhere")
        return localDataSource.getAllData()
    }

    fun fetchFavouriteList(latitude: String, longitude: String): LiveData<List<WeatherResponse>> {
        val exceptionHandlerException = CoroutineExceptionHandler { _, t:Throwable ->
            Log.i(Constants.LOG_TAG,t.message.toString()) }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            if(latitude!="0" && longitude!="0"){
                val response = remoteDataSource.getWeatherService().getAllData(latitude, longitude, Constants.EXCLUDE_MINUTELY, units, language, Constants.WEATHER_API_KEY)
                if (response.isSuccessful) {
                    localDataSource.insertDefault(response.body())
                    Log.i(Constants.LOG_TAG, "success fav")
                }
            }
        }
        Log.i(Constants.LOG_TAG, "outhere fav")
        return localDataSource.getFavList(lat,long)
    }

    fun deleteFromFavourite(lat: String, lon: String) {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteDefault(lat,lon)
        }
    }

    fun insertAlert(alert:AlertModel) {
        CoroutineScope(Dispatchers.IO).launch {
            localAlarmDatabase.insertAlarm(alert)
        }
    }

    fun getAllAlerts(): LiveData<List<AlertModel>> {
            return localAlarmDatabase.getAllAlarms()
    }

    fun refreshCurrentLocation(latitude:String=lat,longitude:String=long){
        runBlocking(Dispatchers.IO) {
            launch {
                try{
            if (lat != null) {
                val response = remoteDataSource.getWeatherService().getAllData(latitude, longitude, Constants.EXCLUDE_MINUTELY, units, language, Constants.WEATHER_API_KEY)
                if (response.isSuccessful) {
                    localDataSource.insertDefault(response.body())
                    localDataSource.deleteDefault(oldLat,oldLong)
                    Log.i(Constants.LOG_TAG, "success22")
                }
            }
            }catch(e:Exception){
                Log.i(Constants.LOG_TAG,e.message.toString())
            }
        }
        }
        Log.i(Constants.LOG_TAG, "outhere22")
    }

    fun deleteAlarmById(id:String) {
        CoroutineScope(Dispatchers.IO).launch {
            localAlarmDatabase.deleteAlarmById(id)
        }
    }

    fun refreshFavData(lat:String,long:String){
        CoroutineScope(Dispatchers.IO).launch {
                try{
                        val response = remoteDataSource.getWeatherService().getAllData(lat, long, Constants.EXCLUDE_MINUTELY, units, language, Constants.WEATHER_API_KEY)
                        if (response.isSuccessful) {
                            localDataSource.insertDefault(response.body())
                        }
                }catch(e:Exception){
                    Log.i(Constants.LOG_TAG,e.message.toString())
                }
            }
    }

    fun getUnrefreshedData():List<WeatherResponse>?{
        var unrefreshedList:List<WeatherResponse>? = null
        runBlocking(Dispatchers.IO){
            launch {
                unrefreshedList = localDataSource.getFavDataForRefresh()
            }
        }
        return unrefreshedList
    }


}