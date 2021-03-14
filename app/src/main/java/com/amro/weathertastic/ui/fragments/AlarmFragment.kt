package com.amro.weathertastic.ui.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.AlarmFragmentBinding
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.ui.AlarmFragmentDirections
import com.amro.weathertastic.ui.adapters.AlarmRecyclerAdaptor
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.AlarmViewModel
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
    var length:Int?= null


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
        if(switchCase){
            binding.include.onOffSwitch.isChecked = switchCase
            binding.include.cardView2.visibility = View.GONE
            binding.include.groupRB.visibility = View.GONE
            binding.include.alarmStateTV.text = resources.getString(R.string.alarmOn)
            binding.include.alarmTimeTV.text = "11:11"
        }else{
            binding.include.onOffSwitch.isChecked = switchCase
            binding.include.cardView2.visibility = View.VISIBLE
            binding.include.groupRB.visibility = View.VISIBLE
            binding.include.alarmStateTV.text = resources.getString(R.string.alarmOff)
            binding.include.alarmTimeTV.text = "--:--"
        }


        viewModel.getAllData().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it!= null){
                if(it.isNotEmpty()){
//                Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
                alarmList.addAll(it)
                    length = alarmList.size
                    Log.i(Constants.LOG_TAG,"get All Data1  $length" )
                alarmAdaptor.setIncomingList(it)
                    binding.backgroundNoData.visibility = View.GONE
                }else{
                    alarmList.addAll(it)
                    length=it.size
                    Log.i(Constants.LOG_TAG,"get All Data2  $length" )

                    binding.backgroundNoData.visibility = View.VISIBLE
                }
            }else{
                Log.i(Constants.LOG_TAG,"get All Data  $length" )
                length=null
                binding.backgroundNoData.visibility = View.VISIBLE
            }

        })
        binding.favFloatingButton.setOnClickListener(View.OnClickListener {
            findNavController().navigate(AlarmFragmentDirections.actionAlarmFragmentToAddAlarmFragment())
        })

        binding.include.onOffSwitch.setOnClickListener{
            if(!binding.include.onOffSwitch.isChecked){
                length =1;
            }
            if(binding.include.alarmTimeTV.text == "--:--" ){
                Toast.makeText(requireContext(),resources.getString(R.string.cautionTime),Toast.LENGTH_SHORT).show()
                binding.include.onOffSwitch.isChecked=false
            }else if(length== null || length ==0){
                Toast.makeText(requireContext(),resources.getString(R.string.addAlarm),Toast.LENGTH_SHORT).show()
                binding.include.onOffSwitch.isChecked=false
            }else if(binding.include.onOffSwitch.isChecked){
//                Toast.makeText(requireContext(),"Alarm is On",Toast.LENGTH_SHORT).show()
                binding.include.alarmStateTV.text = resources.getString(R.string.alarmOn)
                viewModel.registerAll(context,alarmList,hourDuration,calenderEvent, activity as AppCompatActivity)
                binding.include.cardView2.visibility = View.GONE
                binding.include.groupRB.visibility = View.GONE
                sharedPref?.edit()?.putBoolean(Constants.isSwitchOn,true)?.apply()
                binding.favFloatingButton.visibility = View.GONE
            }else{
//                Toast.makeText(requireContext(),"Alarm is Off",Toast.LENGTH_SHORT).show()
                binding.include.alarmStateTV.text = resources.getString(R.string.alarmOff)
                binding.include.cardView2.visibility = View.VISIBLE
                binding.include.groupRB.visibility = View.VISIBLE
                binding.include.alarmTimeTV.text = "--:--"
                viewModel.unregisterAll(context,alarmList,hourDuration)
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
//                Toast.makeText(requireContext(),"$hourDuration",Toast.LENGTH_SHORT).show()
            }else if(i == R.id.radioButton48){
                hourDuration = 48
//                Toast.makeText(requireContext(),"$hourDuration",Toast.LENGTH_SHORT).show()
            }else{
                hourDuration = 72
//                Toast.makeText(requireContext(),"$hourDuration",Toast.LENGTH_SHORT).show()
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

    fun initRecyclers(){
        binding.alarmRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.alarmRecycler.adapter = alarmAdaptor
    }

}