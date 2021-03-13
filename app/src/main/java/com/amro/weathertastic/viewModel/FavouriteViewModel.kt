package com.amro.weathertastic.viewModel

import android.app.Application
import android.content.Context
import android.location.Geocoder
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.amro.weathertastic.R
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.repository.WeatherRepository
import java.io.IOException
import java.util.*

class FavouriteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(getApplication())

    fun fetchFavouriteList(latitude: String, longitude: String):LiveData<List<WeatherResponse>> {
        return repository.fetchFavouriteList(latitude, longitude)
    }

    fun deleteFromFavourite(lat:String,lon: String){
        repository.deleteFromFavourite(lat,lon)
    }

    fun getCityName(context: Context, savedLang: String, lat: Double, lon:Double, timeZone:String):String{
        var locationAddress = ""
        val geocoder = Geocoder(context, Locale(savedLang));
        try {
            if(savedLang=="ar"){
                locationAddress = geocoder.getFromLocation(lat,lon,1)[0].countryName ?: timeZone
            }else{
                locationAddress = geocoder.getFromLocation(lat,lon,1)[0].adminArea ?: timeZone
                locationAddress += ", ${geocoder.getFromLocation(lat,lon,1)[0].countryName ?: timeZone}"}
        } catch (e: IOException){
            e.printStackTrace()
        }
        return locationAddress
    }

    fun showDeletionDialog(context: Context,lat:String,lon: String){
        val builder = AlertDialog.Builder(context).setCancelable(false)
        builder.setTitle(R.string.caution)
        builder.setMessage(R.string.alertMessage1)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            deleteFromFavourite(lat,lon)
        }
        builder.setNegativeButton(R.string.no) { _, _ ->
        }
        builder.show()
    }
}