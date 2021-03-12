package com.amro.weathertastic.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.AlarmItemBinding
import com.amro.weathertastic.utils.AlarmReceiver
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.viewModel.AlarmViewModel

class AlarmRecyclerAdaptor(val list: ArrayList<AlertModel>,val viewModel: AlarmViewModel) : RecyclerView.Adapter<AlarmRecyclerAdaptor.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(AlarmItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.conditionTV.text = list[position].alertType
        if(list[position].maxMinValue == "max"){
            holder.binding.descriptiomTV.text = "More than ${list[position].thresholdValue}"
        }else{
            holder.binding.descriptiomTV.text = "Less than ${list[position].thresholdValue}"
        }
        setWeekImages(position,holder)

        holder.binding.alarmItemContainer.setOnLongClickListener(View.OnLongClickListener {
            showDeletionDialog(position)
            true
        })
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
}