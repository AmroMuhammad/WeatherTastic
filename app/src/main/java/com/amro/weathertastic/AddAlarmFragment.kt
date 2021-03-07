package com.amro.weathertastic

import android.app.TimePickerDialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.get
import com.amro.weathertastic.databinding.AddAlarmFragmentBinding
import com.amro.weathertastic.utils.Constants
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.text.SimpleDateFormat
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
        viewModel = ViewModelProvider(this).get(AddAlarmViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //creating alarm type spinner
        val dataset = resources.getStringArray(R.array.alertTypes).toList()
        binding.niceSpinner.attachDataSource(dataset);

        binding.fromTimeTV.setOnClickListener {calenderTime(it as TextView,0,0)  }
        binding.toTimeTV.setOnClickListener { calenderTime(it as TextView,12,0) }

        binding.anytimeRB.setOnClickListener {
            binding.fromTimeTV.visibility= GONE
            binding.toTimeTV.visibility= GONE
            timeCondition = "anytime"
            binding.fromTimeTV.text = resources.getString(R.string.ChooseBeginTime)
            binding.toTimeTV.text = resources.getString(R.string.chooseEndTIme)
        }

        binding.periodOfTimeRB.setOnClickListener {
            binding.fromTimeTV.visibility= VISIBLE
            binding.toTimeTV.visibility= VISIBLE
            timeCondition = "period"
        }

    }

    private fun returnChoosenDays():List<String>?{
        binding.dayPicker.locale = Locale.forLanguageTag(lang)
        var selectedDays = binding.dayPicker.selectedDays //to be null
        binding.dayPicker.setDaySelectionChangedListener { it ->
            selectedDays = binding.dayPicker.selectedDays
        }
        var arr = mutableListOf<String>()
        selectedDays.forEach { arr.add(it.name.toLowerCase()) }
        return if(arr.isEmpty()) null else arr
    }

    private fun returnAlarmType():String{
        binding.niceSpinner.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener { parent, _, position, _ ->
                val ara = mapOf("" to "","امطار" to "Rain","درجة حرارة" to "Temperature","رياح" to "Wind","ضباب/شبورة" to "Fog/Mist/Haze","ثلوج" to "Snow","غيوم" to "Cloudiness","عواصف رعدية" to "ThunderStorm")
                alarmType = parent?.getItemAtPosition(position).toString()
                if(lang=="ar"){
                    alarmType = ara[alarmType].toString()
                }
            }
        return alarmType
    }

    private fun calenderTime(textView:TextView,hour:Int,min:Int){
        TimePickerDialog(requireContext(), object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                if(textView.id == R.id.fromTimeTV){
                    calenderFrom.set(Calendar.HOUR_OF_DAY,p1)
                    calenderFrom.set(Calendar.MINUTE,p2)
                    calenderFrom.set(Calendar.SECOND,0)
                    textView.setText(SimpleDateFormat("HH:mm").format(calenderFrom.time))
                }else{
                    calenderTo.set(Calendar.HOUR_OF_DAY,p1)
                    calenderTo.set(Calendar.MINUTE,p2)
                    calenderTo.set(Calendar.SECOND,0)  //check time in api
                    textView.setText(SimpleDateFormat("HH:mm").format(calenderTo.time))
                }
            }
        }, hour, min, false).show()
    }
    }