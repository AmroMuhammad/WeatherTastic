package com.amro.weathertastic.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.amro.weathertastic.model.alarmEntities.AlertModel
import com.amro.weathertastic.repository.WeatherRepository

class AddAlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(getApplication())

    fun insertAlert(alert:AlertModel){
        repository.insertAlert(alert)
    }
}