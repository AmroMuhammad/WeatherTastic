package com.amro.weathertastic.viewModel

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.amro.weathertastic.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    fun createSettingsSharedPreferences(context: Context){
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPref?.edit()
        editor?.putString(Constants.LANGUAGE,Constants.LANGUAGE_VALUE)?.apply()
        editor?.putString(Constants.UNITS,Constants.UNITS_VALUE)?.apply()
    }


    fun dayOrNight(currentTime: String,sunrise:String,sunset:String): String {
        val currentNum = currentTime.substringBefore(":").toInt()
        val sunriseNum = sunrise.substringBefore(":").toInt()
        val sunsetNum = sunset.substringBefore(":").toInt()
        if(currentNum in sunriseNum until sunsetNum){
            return "day"
        }else{
            return "night"
        }
    }



    fun getDayTime():String{
        val calender = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH:MM", Locale("en"));
        Log.i(Constants.LOG_TAG, "${dateFormat.format(calender.time)}")
        return dateFormat.format(calender.time)
    }

    fun setAppLocale(languageCode: String?,context: Context) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i(Constants.LOG_TAG, "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i(Constants.LOG_TAG, "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i(Constants.LOG_TAG, "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun isFirstTime(context: Context):Boolean{
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPref?.edit()
        return if(! sharedPref?.contains(Constants.SETTINGS_IS_FIRST_TIME)!!){
            editor?.putBoolean(Constants.SETTINGS_IS_FIRST_TIME,true)?.apply()
            true
        }else{
            false
        }
    }
}