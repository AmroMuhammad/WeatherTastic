package com.amro.weathertastic.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.amro.weathertastic.viewModel.AddAlarmViewModel
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.AddAlarmFragmentBinding
import com.amro.weathertastic.model.alarmDatabase.AlertModel
import com.amro.weathertastic.utils.Constants
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.util.*

class AddAlarmFragment : Fragment() {

    private var _binding: AddAlarmFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddAlarmViewModel
    private var lang = "en"
    private var alarmType = ""
    private var calenderFrom = Calendar.getInstance()
    private var calenderTo = Calendar.getInstance()
    private var timeCondition = "anytime"
    private var maxOrMin = "min"
    private var thresholdValue = 0.0
    private var savedUnit:String = "metric"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddAlarmFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lang = (activity?.getSharedPreferences(Constants.SHARED_PREF_SETTINGS,Context.MODE_PRIVATE)?.getString(Constants.LANGUAGE,"en").toString())
        savedUnit = (activity?.getSharedPreferences(Constants.SHARED_PREF_SETTINGS,Context.MODE_PRIVATE)?.getString(Constants.UNITS,"metric").toString())
        binding.dayPicker.locale = Locale.forLanguageTag(lang)
        viewModel = ViewModelProvider(this).get(AddAlarmViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //creating alarm type spinner
        val dataset = resources.getStringArray(R.array.alertTypes).toList()
        binding.niceSpinner.attachDataSource(dataset);

        binding.saveBtn.setOnClickListener {
            if(isDataValidated()){
                Toast.makeText(requireContext(),"Data is Validated",Toast.LENGTH_SHORT).show()  //add object to database
                thresholdValue = binding.valueET.text.trim().toString().toDouble()
                val alert = AlertModel(days = returnChoosenDays(),alertType = alarmType,maxMinValue = maxOrMin,thresholdValue = thresholdValue)
                viewModel.insertAlert(alert)
                activity?.onBackPressed()

            }
        }

        binding.radioGroupMaxMin.setOnCheckedChangeListener { radioGroup, i ->
            if(i==R.id.maxRB){
                maxOrMin = "max"
                Toast.makeText(requireContext(),"max",Toast.LENGTH_SHORT).show()
            }
            else{ maxOrMin = "min"
                Toast.makeText(requireContext(),"min",Toast.LENGTH_SHORT).show()
            }
        }

        binding.niceSpinner.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener { parent, _, position, _ ->
                val ara = mapOf("" to "","امطار" to "Rain","درجة حرارة" to "Temperature","رياح" to "Wind","ضباب/شبورة" to "Fog/Mist/Haze","ثلوج" to "Snow","غيوم" to "Cloudiness","عواصف رعدية" to "Thunderstorm")
                alarmType = parent?.getItemAtPosition(position).toString()
                if(lang=="ar"){
                    alarmType = ara[alarmType].toString()
                }
                if(alarmType == "Rain" || alarmType == "Temperature" || alarmType == "Wind" || alarmType == "Cloudiness"){
                    binding.valueET.visibility = VISIBLE
                    binding.radioGroupMaxMin.visibility = VISIBLE
                    binding.unitTV.visibility = VISIBLE
                    binding.valueET.setText("")
                    checkUnit(alarmType)
                }else{
                    binding.valueET.visibility = GONE
                    binding.radioGroupMaxMin.visibility = GONE
                    binding.unitTV.visibility = GONE
                    binding.valueET.setText("0.0")
                }
            }

    }

    private fun checkUnit(alarmType: String) {
        when(alarmType){
            "Rain" -> {
                    if(savedUnit == "metric"){
                        binding.unitTV.text = context?.resources?.getString(R.string.rainUnit)
                    }else{
                        binding.unitTV.text = context?.resources?.getString(R.string.rainUnit)
                    }
            }
            "Temperature" -> {
                if(savedUnit == "metric"){
                    binding.unitTV.text = "C"
                }else{
                    binding.unitTV.text = "F"
                }
            }
            "Wind" -> {
                if(savedUnit == "metric"){
                    binding.unitTV.text = context?.resources?.getString(R.string.meterSec)
                }else{
                    binding.unitTV.text = context?.resources?.getString(R.string.mileHr)
                }
            }
            "Cloudiness" -> {binding.unitTV.text = "%"}
        }
    }

    private fun isDataValidated(): Boolean {
        if(returnChoosenDays() == null){
            Toast.makeText(requireContext(),resources.getString(R.string.weekend),Toast.LENGTH_SHORT).show()
            return false
        }else if(alarmType == ""){
            Toast.makeText(requireContext(),resources.getString(R.string.alertType),Toast.LENGTH_SHORT).show()
            return false
        }else if(binding.valueET.text.trim().isEmpty()){
            Toast.makeText(requireContext(),resources.getString(R.string.threshold),Toast.LENGTH_SHORT).show()
            return false
        }else{
            return true
        }
    }

    private fun returnChoosenDays():List<String>?{
        binding.dayPicker.locale = Locale.forLanguageTag(lang)
        var selectedDays = binding.dayPicker.selectedDays
        binding.dayPicker.setDaySelectionChangedListener { it ->
            selectedDays = binding.dayPicker.selectedDays
        }
        var arr = mutableListOf<String>()
        selectedDays.forEach { arr.add(it.name.toLowerCase()) }
        return if(arr.isEmpty()) null else arr
    }

    }