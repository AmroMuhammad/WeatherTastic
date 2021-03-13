package com.amro.weathertastic.viewModel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.amro.weathertastic.R
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.repository.WeatherRepository
import com.amro.weathertastic.utils.AlarmReceiver
import com.amro.weathertastic.utils.Constants
import java.util.*

class AlarmViewModel(application:Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel
    private val repository = WeatherRepository(getApplication())

    fun getAllData(): LiveData<List<AlertModel>> {
        return repository.getAllAlerts()
    }

    fun deleteAlarmById(id:String){
        return repository.deleteAlarmById(id)
    }

    fun unregisterAll(context: Context?,alarmList:List<AlertModel>,hourDuration:Int){
        for(element in alarmList){
            val notifyIntent = Intent(context, AlarmReceiver::class.java)
            var pendingIntent = PendingIntent.getBroadcast(context,element.id,notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent)
            }
            if(hourDuration == 72){
                pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent)
                }
                pendingIntent = PendingIntent.getBroadcast(context,element.id+2000,notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent)
                }
            }else if(hourDuration == 48){
                pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
    }

    fun registerAll(context: Context?,alarmList:List<AlertModel>,hourDuration:Int,calenderEvent:Calendar,activity:FragmentActivity){
        val notifyIntent = Intent(context, AlarmReceiver::class.java)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for(element in alarmList){
            if(Calendar.getInstance().timeInMillis >= calenderEvent.timeInMillis){
                notifyIntent.putExtra(Constants.ALARM_ID,element.id)
                var time = calenderEvent.timeInMillis
                calenderEvent.timeInMillis = time.plus(Constants.HOUR_24_IN_SECONDS)
                var pendingIntent = PendingIntent.getBroadcast(context,element.id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
                Log.i(Constants.LOG_TAG,"twentyfour${calenderEvent.timeInMillis}")
                if(hourDuration == 72){
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+ Constants.HOUR_24_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"fourtyeight${calenderEvent.timeInMillis+ Constants.HOUR_24_IN_SECONDS}")
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+2000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+ Constants.HOUR_48_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"sevenTwo${calenderEvent.timeInMillis+ Constants.HOUR_48_IN_SECONDS}")
                }else if(hourDuration == 48){
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+ Constants.HOUR_24_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"fourtyeight${calenderEvent.timeInMillis+ Constants.HOUR_24_IN_SECONDS}")
                }
            }else{
                notifyIntent.putExtra(Constants.ALARM_ID,element.id)
                var pendingIntent = PendingIntent.getBroadcast(activity,element.id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
                Log.i(Constants.LOG_TAG,"twentyfour${calenderEvent.timeInMillis}")
                if(hourDuration == 72){
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+ Constants.HOUR_24_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"fourtyeight${calenderEvent.timeInMillis+ Constants.HOUR_24_IN_SECONDS}")
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+2000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+ Constants.HOUR_48_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"sevenTwo${calenderEvent.timeInMillis+ Constants.HOUR_48_IN_SECONDS}")
                }else if(hourDuration == 48){
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+ Constants.HOUR_24_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"fourtyeight${calenderEvent.timeInMillis+ Constants.HOUR_24_IN_SECONDS}")
                }
            }
        }
    }

    fun checkUnit(alertType:String,context: Context,savedUnit:String):String{
        when(alertType){
            "Rain"->{
                return context.resources.getString(R.string.rainUnit)
            }
            "Temperature"->{
                if(savedUnit == "metric"){
                    return "C"
                }
                else{
                    return "F"
                }
            }
            "Wind"->{
                if(savedUnit == "metric"){
                    return context.resources.getString(R.string.meterSec)
                }
                else{
                    return context.resources.getString(R.string.mileHr)
                }
            }
            "Cloudiness"->{
                return "%"
            }
        }
        return ""
    }
}