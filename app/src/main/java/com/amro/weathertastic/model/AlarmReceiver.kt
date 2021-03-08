package com.amro.weathertastic.model

import android.R
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.amro.weathertastic.model.alarmEntities.AlertDataSource
import com.amro.weathertastic.model.alarmEntities.AlertModel
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.model.localDataSource.LocalDataSource
import com.amro.weathertastic.repository.WeatherRepository
import com.amro.weathertastic.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates


class AlarmReceiver : BroadcastReceiver() {
    lateinit var mainContext:Context
    lateinit var repo:WeatherRepository
    lateinit var requiredWeatherResponse:WeatherResponse
    lateinit var requiredAlertResponse: AlertModel
    var alarmId by Delegates.notNull<Int>()
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
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context, "helllo", Toast.LENGTH_SHORT).show()
        Log.i(Constants.LOG_TAG, "hello from alarmReceiver")
        Log.i(Constants.LOG_TAG, requiredWeatherResponse.timezone.toString())
        Log.i(Constants.LOG_TAG, requiredAlertResponse.alertType)
        sendNotification()
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
        builder.setContentTitle("Weather Alert")
        builder.setContentText("Current Weather is")
        builder.setSmallIcon(R.drawable.ic_menu_add)
        val notification: Notification = builder.build()
        manager.notify(10, notification)
    }


    private fun checkData(weatherResponse: WeatherResponse,alertModel: AlertModel){
        when(alertModel.alertType){

        }
    }
}