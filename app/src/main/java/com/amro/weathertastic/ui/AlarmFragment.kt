package com.amro.weathertastic.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amro.weathertastic.R
import com.amro.weathertastic.viewModel.AlarmViewModel
import com.amro.weathertastic.databinding.AlarmFragmentBinding
import com.amro.weathertastic.utils.AlarmReceiver
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
    private lateinit var sharedPref:SharedPreferences
    private var switchCase = false
    private lateinit var alarmAdaptor: AlarmRecyclerAdaptor


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AlarmFragmentBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
        alarmAdaptor = AlarmRecyclerAdaptor(ArrayList(),viewModel)
        initRecyclers()
        sharedPref = activity?.getSharedPreferences(Constants.SHARED_PREF_SETTINGS,Context.MODE_PRIVATE)!!
        switchCase = sharedPref!!.getBoolean(Constants.isSwitchOn,false)
//        if(switchCase){
//            binding.include.onOffSwitch.isChecked = switchCase
//            binding.include.cardView2.visibility = View.GONE
//        }else{
//            binding.include.onOffSwitch.isChecked = switchCase
//            binding.include.cardView2.visibility = View.VISIBLE
//
//        }


        viewModel.getAllData().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it!= null){
                if(! it.isEmpty()){
                Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
                alarmList.addAll(it)
                alarmAdaptor.setIncomingList(it)
                    binding.backgroundNoData.visibility = View.GONE

                }else{
                    binding.backgroundNoData.visibility = View.VISIBLE

                }
            }else{
                binding.backgroundNoData.visibility = View.VISIBLE
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
                binding.include.groupRB.visibility = View.GONE
                sharedPref?.edit()?.putBoolean(Constants.isSwitchOn,true)?.apply()
                binding.favFloatingButton.visibility = View.GONE
            }else{
                Toast.makeText(requireContext(),"Alarm is Off",Toast.LENGTH_SHORT).show()
                binding.include.alarmStateTV.text = resources.getString(R.string.alarmOff)
                binding.include.cardView2.visibility = View.VISIBLE
                binding.include.groupRB.visibility = View.VISIBLE
                binding.include.alarmTimeTV.text = "--:--"
                unregisterAll()
                sharedPref?.edit()?.putBoolean(Constants.isSwitchOn,false)?.apply()
                binding.favFloatingButton.visibility = View.VISIBLE
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
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+Constants.HOUR_24_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"fourtyeight${calenderEvent.timeInMillis+Constants.HOUR_24_IN_SECONDS}")
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+2000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+Constants.HOUR_48_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"sevenTwo${calenderEvent.timeInMillis+Constants.HOUR_48_IN_SECONDS}")
                }else if(hourDuration == 48){
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+Constants.HOUR_24_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"fourtyeight${calenderEvent.timeInMillis+Constants.HOUR_24_IN_SECONDS}")
                }
            }else{
                notifyIntent.putExtra(Constants.ALARM_ID,element.id)
                var pendingIntent = PendingIntent.getBroadcast(activity,element.id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
                Log.i(Constants.LOG_TAG,"twentyfour${calenderEvent.timeInMillis}")
                if(hourDuration == 72){
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+Constants.HOUR_24_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"fourtyeight${calenderEvent.timeInMillis+Constants.HOUR_24_IN_SECONDS}")
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+2000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+Constants.HOUR_48_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"sevenTwo${calenderEvent.timeInMillis+Constants.HOUR_48_IN_SECONDS}")
                }else if(hourDuration == 48){
                    pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis+Constants.HOUR_24_IN_SECONDS,pendingIntent)
                    Log.i(Constants.LOG_TAG,"fourtyeight${calenderEvent.timeInMillis+Constants.HOUR_24_IN_SECONDS}")
                }
            }
        }
    }

    private fun unregisterAll(){
        for(element in alarmList){
        val notifyIntent = Intent(context, AlarmReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(context,element.id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
            if(hourDuration == 72){
            pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent)
            }
            pendingIntent = PendingIntent.getBroadcast(context,element.id+2000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent)
            }
            }else if(hourDuration == 48){
            pendingIntent = PendingIntent.getBroadcast(context,element.id+1000,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent)
            }
            }
        }
    }

    fun initRecyclers(){
        binding.alarmRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.alarmRecycler.adapter = alarmAdaptor
    }



}