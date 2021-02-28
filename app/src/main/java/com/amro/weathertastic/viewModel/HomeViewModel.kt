package com.amro.weathertastic.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.repository.WeatherRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel
    private val repository = WeatherRepository(getApplication())

    fun fetchDailyData():LiveData<WeatherResponse>{
        return repository.loadCurrentData()
    }
}