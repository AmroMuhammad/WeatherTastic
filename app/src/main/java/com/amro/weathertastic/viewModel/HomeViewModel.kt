package com.amro.weathertastic.viewModel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.model.remoteDataSource.RemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel
    val dailyWeatherData = MutableLiveData<WeatherResponse>()

    fun fetchDailyData(){
        val context = getApplication<Application>()
        val lat = context.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION,MODE_PRIVATE).getString(
            Constants.CURRENT_LATITUDE,"0").toString()
        val long = context.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION,MODE_PRIVATE).getString(
            Constants.CURRENT_LONGITUDE,"0").toString()

        CoroutineScope(Dispatchers.IO).launch {
            val response = RemoteDataSource.getWeatherService().getAllData(lat, long, Constants.EXCLUDE_MINUTELY, "default", "en", Constants.WEATHER_API_KEY)
            if(response.isSuccessful){
                dailyWeatherData.postValue(response.body())
            }else{
                Log.i(Constants.LOG_TAG,"error")
            }
        }
    }
}