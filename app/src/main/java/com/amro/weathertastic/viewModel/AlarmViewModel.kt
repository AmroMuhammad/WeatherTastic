package com.amro.weathertastic.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.repository.WeatherRepository

class AlarmViewModel(application:Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel
    private val repository = WeatherRepository(getApplication())

    fun getAllData(): LiveData<List<AlertModel>> {
        return repository.getAllAlerts()
    }

    fun deleteAlarmById(id:String){
        return repository.deleteAlarmById(id)
    }
}