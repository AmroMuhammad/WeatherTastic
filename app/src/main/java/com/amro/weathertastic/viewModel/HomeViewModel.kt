package com.amro.weathertastic.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.repository.WeatherRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(getApplication())

    fun fetchDailyData():LiveData<List<WeatherResponse>>{
        return repository.loadAllData()
    }
}