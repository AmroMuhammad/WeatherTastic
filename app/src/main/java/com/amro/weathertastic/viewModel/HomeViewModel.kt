package com.amro.weathertastic.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.amro.weathertastic.R
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.repository.WeatherRepository
import com.amro.weathertastic.utils.Constants
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(getApplication())

    fun loadAllData():LiveData<List<WeatherResponse>>{
        return repository.loadAllData()
    }

    fun refreshFavData(lat:String,lon:String){
        repository.refreshFavData(lat,lon)
    }

    fun getUnrefreshedData():List<WeatherResponse>?{
        return repository.getUnrefreshedData()
    }

    fun refreshCurrentLocation(lat: String,lon: String) {
        repository.refreshCurrentLocation(lat,lon)
    }

    fun getIcon(id:String): Int{
        when(id){
            "01d"->{
                return R.raw.clearsky
            }
            "01n"->{
                return R.raw.clearsky
            }
            "02d"->{
                return R.raw.scattered
            }
            "02n"->{
                return R.raw.scattered
            }
            "03d","03n","04d","04n"->{
                return R.raw.scattered
            }
            "09d","10d"->{
                return R.raw.rain
            }
            "09n","10n"->{
                return R.raw.rain
            }
            "11d"->{
                return R.raw.thunder
            }
            "11n"->{
                return R.raw.thunder
            }
            "13d"->{
                return R.raw.snow
            }
            "13n"->{
                return R.raw.snow
            }
            "50d"->{
                return R.raw.mist
            }
            "50n"->{
                return R.raw.mist
            }
            else->{
                return R.raw.clearsky
            }
        }
    }

    fun getDayTime(dt:Int,timezoneOffset:Int,savedLang:String):String{
        val calender = Calendar.getInstance()
        calender.timeInMillis = (dt.plus(timezoneOffset)?.minus(7200)?.toLong() ?: 10)*1000L
        val dateFormat = SimpleDateFormat("EE, HH:MM", Locale(savedLang));
        return dateFormat.format(calender.time)
    }

    fun getTimeInCalender(dt:Int,timezoneOffset:Int): Calendar {
        val calenderr = Calendar.getInstance()
        calenderr.timeInMillis = (dt.plus(timezoneOffset)?.minus(7200)?.toLong() ?: 10)*1000L
        return calenderr
    }

    fun sunRiseTime(sunrise:Int,timezoneOffset:Int,savedLang:String):String{
        val calender = Calendar.getInstance()
        calender.timeInMillis = (sunrise.plus(timezoneOffset)?.minus(7200)?.toLong() ?: 10)*1000L
        val dateFormat = SimpleDateFormat("HH:MM", Locale(savedLang));
        return dateFormat.format(calender.time)
    }

    fun sunsetTime(sunset:Int,timezoneOffset:Int,savedLang:String):String{
        val calender = Calendar.getInstance()
        calender.timeInMillis = (sunset.plus(timezoneOffset)?.minus(7200)?.toLong() ?: 10)*1000L
        val dateFormat = SimpleDateFormat("HH:MM", Locale(savedLang));
        return dateFormat.format(calender.time)
    }

    fun dayOrNight(currentTime: String,sunrise:String,sunset:String): String {
        val currentNum = currentTime.substringAfter(" ").substringBefore(":")
        val sunriseNum = sunrise.substringBefore(":")
        val sunsetNum = sunset.substringBefore(":")
        if(currentNum in sunriseNum..sunsetNum){
            return "day"
        }else{
            return "night"
        }
    }

    fun getCityName(context:Context,savedLang: String,lat: Double,lon:Double,timeZone:String):String{
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

    fun getDayTimeDaily(dt:Int,timezoneOffset:Int,savedLang:String):String{
        val calender = Calendar.getInstance()
        calender.timeInMillis = (dt.plus(timezoneOffset)?.minus(7200)?.toLong() ?: 10)*1000L
        val dateFormat = SimpleDateFormat("EE, dd-MM-yyyy", Locale(savedLang));
        return dateFormat.format(calender.time)
    }
}
