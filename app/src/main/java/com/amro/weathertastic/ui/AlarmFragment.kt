package com.amro.weathertastic.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.amro.weathertastic.R
import com.amro.weathertastic.viewModel.AlarmViewModel
import com.amro.weathertastic.databinding.AlarmFragmentBinding
import com.amro.weathertastic.model.AlarmReceiver
import com.amro.weathertastic.model.alarmEntities.AlertModel
import com.amro.weathertastic.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmFragment : Fragment() {

    private lateinit var viewModel: AlarmViewModel
    private var _binding: AlarmFragmentBinding? = null
    private val binding get() = _binding!!
    private var calenderEvent = Calendar.getInstance()
    private var hourDuration = 24;
    private var alarmList = ArrayList<AlertModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AlarmFragmentBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
        viewModel.getAllData().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it!= null){
                Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
                alarmList.addAll(it)
            }

        })
        binding.favFloatingButton.setOnClickListener(View.OnClickListener {
            findNavController().navigate(AlarmFragmentDirections.actionAlarmFragmentToAddAlarmFragment())
        })

        binding.include.onOffSwitch.setOnClickListener{
            if(binding.include.alarmTimeTV.text == "--:--"){
                Toast.makeText(requireContext(),resources.getString(R.string.cautionTime),Toast.LENGTH_SHORT).show()
                binding.include.onOffSwitch.isChecked=false
            }else if(alarmList== null || alarmList.size ==0){
                Toast.makeText(requireContext(),"kindly add alert",Toast.LENGTH_SHORT).show()
                binding.include.onOffSwitch.isChecked=false
            }else if(binding.include.onOffSwitch.isChecked){
                Toast.makeText(requireContext(),"Alarm is On",Toast.LENGTH_SHORT).show()
                binding.include.alarmStateTV.text = resources.getString(R.string.alarmOn)
                registerAll()
                binding.include.cardView2.visibility = View.GONE
            }else{
                Toast.makeText(requireContext(),"Alarm is Off",Toast.LENGTH_SHORT).show()
                binding.include.alarmStateTV.text = resources.getString(R.string.alarmOff)
                binding.include.cardView2.visibility = View.VISIBLE

                //unregister all
            }
        }

        binding.include.alarmTimeTV.setOnClickListener {
            calenderTime(it as TextView,calenderEvent.time.hours,calenderEvent.time.minutes)
        }

        binding.include.groupRB.setOnCheckedChangeListener { radioGroup, i ->
            if(i == R.id.radioButton24){
                hourDuration = 24
                Toast.makeText(requireContext(),"$hourDuration",Toast.LENGTH_SHORT).show()
            }else if(i == R.id.radioButton48){
                hourDuration = 48
                Toast.makeText(requireContext(),"$hourDuration",Toast.LENGTH_SHORT).show()
            }else{
                hourDuration = 72
                Toast.makeText(requireContext(),"$hourDuration",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calenderTime(textView: TextView,hour:Int,min:Int){
        TimePickerDialog(requireContext(), object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                    calenderEvent = Calendar.getInstance()
                    calenderEvent.set(Calendar.HOUR_OF_DAY,p1)
                    calenderEvent.set(Calendar.MINUTE,p2)
                    calenderEvent.set(Calendar.SECOND,0)
                    textView.setText(SimpleDateFormat("HH:mm").format(calenderEvent.time))
                Log.i(Constants.LOG_TAG,"${calenderEvent.timeInMillis}")

            }
        }, hour, min, false).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun registerAll(){
        val notifyIntent = Intent(context,AlarmReceiver::class.java)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for(element in alarmList){
            if(Calendar.getInstance().timeInMillis >= calenderEvent.timeInMillis){
                notifyIntent.putExtra(Constants.ALARM_ID,element.id)
                var time = calenderEvent.timeInMillis
                calenderEvent.timeInMillis = time.plus(Constants.HOUR_24_IN_SECONDS)
                val pendingIntent = PendingIntent.getBroadcast(context,element.id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
                Log.i(Constants.LOG_TAG,"${calenderEvent.timeInMillis}")
            }else{
                notifyIntent.putExtra(Constants.ALARM_ID,element.id)
                val pendingIntent = PendingIntent.getBroadcast(activity,element.id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
                Log.i(Constants.LOG_TAG,"${calenderEvent.timeInMillis}")
            }
        }
    }



}