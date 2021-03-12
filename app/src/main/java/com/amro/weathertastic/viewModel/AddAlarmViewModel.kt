package com.amro.weathertastic.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.repository.WeatherRepository

class AddAlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(getApplication())

    fun insertAlert(alert:AlertModel){
        repository.insertAlert(alert)
    }
}