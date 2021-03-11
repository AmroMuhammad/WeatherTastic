package com.amro.weathertastic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.DailyItemBinding
import com.amro.weathertastic.databinding.HourlyItemBinding
import com.amro.weathertastic.databinding.WeatherItemBinding
import com.amro.weathertastic.model.entities.DailyItem
import com.amro.weathertastic.utils.Constants
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DailyRecyclerAdapter(val list : List<DailyItem?>, private val timezoneOffset: Int) : RecyclerView.Adapter<DailyRecyclerAdapter.ViewHolder>() {
    private lateinit var context:Context
    private lateinit var savedUnit: String
    private lateinit var savedLang: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        savedUnit = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.UNITS, "metric").toString()
        savedLang = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.LANGUAGE, "en").toString()
        return ViewHolder(DailyItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.temp?.max?.toInt());
        holder.binding.degreeMax.text = araNum.toString()
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.temp?.min?.toInt());
        holder.binding.degreeMin.text = araNum.toString()
        holder.binding.condTV.text = list[position]?.weather?.get(0)?.description.toString()
        holder.binding.cityTimeTV.text = getDayTime(position)
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.windSpeed?.toInt());
        holder.binding.windValueTV.text = araNum.toString()
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.clouds);
        holder.binding.cloudValueTV.text = araNum.toString()
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.humidity);
        holder.binding.humValueTV.text = araNum.toString()
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.pressure);
        holder.binding.pressvalueTV.text = araNum.toString()
        holder.binding.condImg.setAnimation(getIcon(list[position]?.weather?.get(0)?.icon!!))


        if (savedUnit == "metric") {
            //wind and pressure
            holder.binding.windUnitTV.text = context.resources.getString(R.string.meterSec)
            holder.binding.tempDegreeTV.setImageResource(R.drawable.ic_celsius)

        } else {
            holder.binding.windUnitTV.text = context.resources.getString(R.string.mileHr)
            holder.binding.tempDegreeTV.setImageResource(R.drawable.ic_fahrenheit)
        }

    }

    override fun getItemCount()= list.size


    inner class ViewHolder (val binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)

    private fun getDayTime(position: Int):String{
        val calender = Calendar.getInstance()
        calender.timeInMillis = (list[position]?.dt?.plus(timezoneOffset)?.minus(7200)?.toLong() ?: 10)*1000L
        val dateFormat = SimpleDateFormat("EE, HH:MM",Locale(savedLang));
        return dateFormat.format(calender.time)
    }

    fun getIcon(id:String): Int{
        when(id){
            "01d"->{
                return R.raw.clearsky
            }
            "01n"->{
                return R.raw.clearsky
            }
            "02d"->{
                return R.raw.scattered
            }
            "02n"->{
                return R.raw.scattered
            }
            "03d","03n","04d","04n"->{
                return R.raw.scattered
            }
            "09d","10d"->{
                return R.raw.rain
            }
            "09n","10n"->{
                return R.raw.rain
            }
            "11d"->{
                return R.raw.thunder
            }
            "11n"->{
                return R.raw.thunder
            }
            "13d"->{
                return R.raw.snow
            }
            "13n"->{
                return R.raw.snow
            }
            "50d"->{
                return R.raw.mist
            }
            "50n"->{
                return R.raw.mist
            }
            else->{
                return R.raw.clearsky
            }
        }
    }
}