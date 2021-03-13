package com.amro.weathertastic.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.AlarmItemBinding
import com.amro.weathertastic.utils.AlarmReceiver
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.AlarmViewModel

class AlarmRecyclerAdaptor(val list: ArrayList<AlertModel>,val viewModel: AlarmViewModel) : RecyclerView.Adapter<AlarmRecyclerAdaptor.ViewHolder>() {
    private lateinit var context: Context
    private var savedUnit:String = "metric"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        savedUnit = (context?.getSharedPreferences(Constants.SHARED_PREF_SETTINGS,Context.MODE_PRIVATE)?.getString(Constants.UNITS,"metric").toString())
        return ViewHolder(AlarmItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(list[position].maxMinValue == "max"){
            holder.binding.descriptiomTV.text = context.resources.getString(R.string.checkMore)+" ${list[position].thresholdValue} "+viewModel.checkUnit(list[position].alertType,context,savedUnit)
        }else{
            holder.binding.descriptiomTV.text = context.resources.getString(R.string.checkMore)+" ${list[position].thresholdValue} "+viewModel.checkUnit(list[position].alertType,context,savedUnit)
        }
        setWeekImages(position,holder)

        holder.binding.alarmItemContainer.setOnLongClickListener(View.OnLongClickListener {
            showDeletionDialog(position)
            true
        })
        holder.binding.condImg.setAnimation(getIcon(list[position].alertType,holder))
    }

    private fun showDeletionDialog(position: Int){
        val builder = AlertDialog.Builder(context).setCancelable(false)
        builder.setTitle(R.string.caution)
        builder.setMessage(R.string.alertMessage1)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            unregisterAlarm(list[position].id)
            viewModel.deleteAlarmById(list[position].id.toString())

        }
        builder.setNegativeButton(R.string.no) { _, _ ->
        }
        builder.show()
    }

    private fun unregisterAlarm(id:Int){
            val notifyIntent = Intent(context, AlarmReceiver::class.java)
            var pendingIntent = PendingIntent.getBroadcast(context,id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent)
            }
                pendingIntent = PendingIntent.getBroadcast(context,id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent)
                }
                pendingIntent = PendingIntent.getBroadcast(context,id+2000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent)
                }
    }

    private fun setWeekImages(pos:Int,holder: ViewHolder) {
        for(day in list[pos].days!!){
            when(day){
                "saturday" -> { holder.binding.saturdayImg.visibility= View.VISIBLE }
                "sunday" -> { holder.binding.sundayImg.visibility= View.VISIBLE }
                "monday" -> { holder.binding.mondayImg.visibility= View.VISIBLE }
                "tuesday" -> { holder.binding.tuesdayImg.visibility= View.VISIBLE }
                "wednesday" -> { holder.binding.wednesdayImg.visibility= View.VISIBLE }
                "thursday" -> { holder.binding.thursdayImg.visibility= View.VISIBLE }
                "friday" -> { holder.binding.fridayImg.visibility= View.VISIBLE }
            }
        }
    }

    override fun getItemCount() = list.size

    fun setIncomingList(incomingList: List<AlertModel>) {
        list.clear()
        list.addAll(incomingList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: AlarmItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun getIcon(id:String,holder: ViewHolder): Int{
        when(id){
            "Rain"->{
                holder.binding.descriptiomTV.visibility = View.VISIBLE
                holder.binding.conditionTV.text = context.resources.getString(R.string.rain)
                return R.raw.rain
            }
            "Temperature"->{
                holder.binding.descriptiomTV.visibility = View.VISIBLE
                holder.binding.conditionTV.text = context.resources.getString(R.string.temp)
                return R.raw.clearsky
            }
            "Wind"->{
                holder.binding.descriptiomTV.visibility = View.VISIBLE
                holder.binding.conditionTV.text = context.resources.getString(R.string.wind)
                return R.raw.mist
            }
            "Fog/Mist/Haze"->{
                holder.binding.descriptiomTV.visibility = View.GONE
                holder.binding.conditionTV.text = context.resources.getString(R.string.fog)
                return R.raw.mist
            }
            "Snow"->{
                holder.binding.descriptiomTV.visibility = View.GONE
                holder.binding.conditionTV.text = context.resources.getString(R.string.snow)
                return R.raw.snow
            }
            "Cloudiness"->{
                holder.binding.descriptiomTV.visibility = View.VISIBLE
                holder.binding.conditionTV.text = context.resources.getString(R.string.cloudiness)
                return R.raw.clearsky
            }
            "Thunderstorm"->{
                holder.binding.descriptiomTV.visibility = View.GONE
                holder.binding.conditionTV.text = context.resources.getString(R.string.thunderstorm)
                return R.raw.thunder
            }
            else->{
                return R.raw.clearsky
            }
        }
    }
}