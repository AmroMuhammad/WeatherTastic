package com.amro.weathertastic.utils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import com.amro.weathertastic.R
import com.amro.weathertastic.model.alarmDatabase.AlertDataSource
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.model.entities.DailyItem
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.model.localDataSource.LocalDataSource
import com.amro.weathertastic.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class AlarmReceiver : BroadcastReceiver() {
    lateinit var mainContext:Context
    lateinit var repo:WeatherRepository
    lateinit var requiredWeatherResponse:WeatherResponse
    lateinit var requiredAlertResponse: AlertModel
    var alarmId by Delegates.notNull<Int>()
    var result = ""
    override fun onReceive(context: Context, intent: Intent) {
        mainContext = context
        alarmId = intent.getExtras()!!.getInt(Constants.ALARM_ID)
        Log.i(Constants.LOG_TAG, intent.getExtras()!!.getInt(Constants.ALARM_ID).toString())
        repo = WeatherRepository(context.applicationContext as Application)
        repo.getCurrentForBroadCast()

        val lat = context.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION,Context.MODE_PRIVATE).getString(Constants.CURRENT_LATITUDE,"0.0")
        val lon = context.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION,Context.MODE_PRIVATE).getString(Constants.CURRENT_LONGITUDE,"0.0")

        runBlocking(Dispatchers.IO) {
            launch {
                requiredWeatherResponse = LocalDataSource.getInstance(context.applicationContext as Application).getCurrentForBroadCast(lat!!,lon!!)
                requiredAlertResponse = AlertDataSource.getInstance(context.applicationContext as Application).getSingleAlarm(alarmId.toString())
            }
        }
        Log.i(Constants.LOG_TAG, "hello from alarmReceiver")
        Log.i(Constants.LOG_TAG, requiredWeatherResponse.timezone.toString())
        Log.i(Constants.LOG_TAG, requiredAlertResponse.alertType)
        checkData(requiredAlertResponse)
    }

    private fun sendNotification() {
        var builder = Notification.Builder(mainContext)
        val manager = mainContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "WeatherTastic", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(mainContext, Constants.NOTIFICATION_CHANNEL_ID)
        } else {
            builder = Notification.Builder(mainContext)
        }
        builder.setContentTitle("WeatherTastic")
        builder.setContentText("Weather Report")
        builder.setSmallIcon(R.drawable.ic_icon)
        builder.setStyle(Notification.BigTextStyle().bigText(result))
        val notification: Notification = builder.build()
        manager.notify(requiredAlertResponse.id, notification)
    }


    private fun checkData(alertModel: AlertModel){
        for(day in alertModel.days!!){
            when(day){
                "saturday" -> {
                    checkWeatherDate(day,alertModel.alertType)}
                "sunday" -> {
                    checkWeatherDate(day,alertModel.alertType)}
                "monday" -> {
                    checkWeatherDate(day,alertModel.alertType)}
                "tuesday" -> {
                    checkWeatherDate(day,alertModel.alertType)}
                "wednesday" -> {
                    checkWeatherDate(day,alertModel.alertType)}
                "thursday" -> {
                    checkWeatherDate(day,alertModel.alertType)}
                "friday" -> {
                    checkWeatherDate(day,alertModel.alertType)}
            }
        }
        if(result != ""){
            sendNotification()
        }
    }



    private fun checkWeatherDate(reqDay:String,alertType:String){
        for(element in requiredWeatherResponse.daily!!){
            if(reqDay.toLowerCase() == getDayFromWeatherResponse(element!!.dt!!.toLong())){
                checkAlertTypeOfDate(requiredWeatherResponse.daily!!.indexOf(element),alertType,reqDay)
            }
        }
    }

    private fun getDayFromWeatherResponse(dt:Long):String{
        val calender = Calendar.getInstance()
        calender.timeInMillis = (dt)*1000L
        val dateFormat = SimpleDateFormat("EEEE");
        return dateFormat.format(calender.time).toLowerCase()
    }

    private fun checkAlertTypeOfDate(dailyNum:Int,alertType:String,reqDay: String){
        val item = requiredWeatherResponse.daily?.get(dailyNum)!!
        when(alertType){
            "Rain" -> { checkMaxMinRain(item,reqDay) }
            "Temperature" -> { checkMaxMinTemp(item,reqDay) }
            "Wind" -> { checkMaxMinWind(item,reqDay) }
            "Fog/Mist/Haze" -> { checkMaxMinHaze(item,reqDay) }
            "Snow" -> { checkMaxMinSnow(item,reqDay) }
            "Cloudiness" -> { checkMaxMinCloudness(item,reqDay) }
            "Thunderstorm" -> { checkMaxMinThunder(item,reqDay) }
        }

    }

    private fun checkMaxMinRain(item:DailyItem,reqDay: String){
        if(item?.weather?.get(0)?.main == "Rain") {
            if (requiredAlertResponse.maxMinValue == "max") {
                if (requiredAlertResponse.thresholdValue <= item.rain!!) {
                    result += "\n${reqDay} Rain is ${item.rain} more than ${requiredAlertResponse.thresholdValue}"
                }
            } else {
                if (requiredAlertResponse.thresholdValue > item.rain!!) {
                    result += "\n${reqDay} Rain is ${item.rain} less than ${requiredAlertResponse.thresholdValue}"
                }
            }
        }
    }

    private fun checkMaxMinTemp(item:DailyItem,reqDay: String){
            if (requiredAlertResponse.maxMinValue == "max") {
                if (requiredAlertResponse.thresholdValue <= item.temp?.day!!) {
                    result += "\n${reqDay} Temperature is ${item.temp.day} more than ${requiredAlertResponse.thresholdValue}"
                }
            } else {
                if (requiredAlertResponse.thresholdValue > item.temp?.day!!) {
                    result += "\n${reqDay} Temperature is ${item.temp.day} less than ${requiredAlertResponse.thresholdValue}"
                }
            }

    }

    private fun checkMaxMinWind(item:DailyItem,reqDay: String){
            if (requiredAlertResponse.maxMinValue == "max") {
                if (requiredAlertResponse.thresholdValue <= item.windSpeed!!) {
                    result += "\n${reqDay} Wind is ${item.windSpeed} more than ${requiredAlertResponse.thresholdValue}"
                }
            } else {
                if (requiredAlertResponse.thresholdValue > item.windSpeed!!) {
                    result += "\n${reqDay} Wind is ${item.windSpeed} less than ${requiredAlertResponse.thresholdValue}"
                }
            }
    }

    private fun checkMaxMinHaze(item:DailyItem,reqDay: String){
        if(item.weather?.get(0)?.main == "Haze" ||item.weather?.get(0)?.main == "Mist" ||item.weather?.get(0)?.main == "Fog") {
            result += "\n${reqDay} This day has Haze"
        }
    }

    private fun checkMaxMinSnow(item:DailyItem,reqDay: String){
        if(item.weather?.get(0)?.main == "Snow") {
            result += "\n${reqDay} This day has Snow"
        }
    }

    private fun checkMaxMinCloudness(item:DailyItem,reqDay: String){
        if(item.weather?.get(0)?.main == "Clouds") {
            if (requiredAlertResponse.maxMinValue == "max") {
                if (requiredAlertResponse.thresholdValue <= item.clouds!!) {
                    result += "\n${reqDay} Cloudiness is ${item.clouds} more than ${requiredAlertResponse.thresholdValue}"
                }
            } else {
                if (requiredAlertResponse.thresholdValue > item.temp?.day!!) {
                    result += "\n${reqDay} Cloudiness is ${item.clouds} less than ${requiredAlertResponse.thresholdValue}"
                }
            }
        }
    }

    private fun checkMaxMinThunder(item:DailyItem,reqDay: String){
        if(item.weather?.get(0)?.main == "Thunderstorm") {
            result += "\n${reqDay} This day has ThunderStorm"
        }
    }





}