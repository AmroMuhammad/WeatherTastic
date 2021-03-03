package com.amro.weathertastic.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.repository.WeatherRepository

class FavouriteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(getApplication())

    fun fetchFavouriteList(latitude: String, longitude: String):LiveData<List<WeatherResponse>> {
        return repository.fetchFavouriteList(latitude, longitude)
    }
}