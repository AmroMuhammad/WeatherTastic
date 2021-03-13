package com.amro.weathertastic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.HourlyItemBinding
import com.amro.weathertastic.databinding.WeatherItemBinding
import com.amro.weathertastic.model.entities.DailyItem
import com.amro.weathertastic.model.entities.HourlyItem
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.HomeViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class HourlyRecyclerAdapter(var list: List<HourlyItem?>, private val timezoneOffset: Int,val viewModel: HomeViewModel) : RecyclerView.Adapter<HourlyRecyclerAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var savedUnit: String
    private lateinit var savedLang: String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        savedUnit = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.UNITS, "metric").toString()
        savedLang = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.LANGUAGE, "en").toString()
        return ViewHolder(HourlyItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.temp);
        holder.binding.degreeMin.text = araNum.toString()
        holder.binding.condTV.text = list[position]?.weather?.get(0)?.description.toString()
        holder.binding.cityTimeTV.text = viewModel.getDayTime(list[position]?.dt!!,timezoneOffset,savedLang)
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.windSpeed);
        holder.binding.windValueTV.text = araNum.toString()
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.clouds);
        holder.binding.cloudValueTV.text = araNum.toString()
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.humidity);
        holder.binding.humValueTV.text = araNum.toString()
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.pressure);
        holder.binding.pressvalueTV.text = araNum.toString()
        holder.binding.condImg.setAnimation(viewModel.getIcon(list[position]?.weather?.get(0)?.icon!!))

        if (savedUnit == "metric") {
            //wind and pressure
            holder.binding.windUnitTV.text = context.resources.getString(R.string.meterSec)
            holder.binding.tempDegreeTV.setImageResource(R.drawable.ic_celsius)

        } else {
            holder.binding.windUnitTV.text = context.resources.getString(R.string.mileHr)
            holder.binding.tempDegreeTV.setImageResource(R.drawable.ic_fahrenheit)
        }


    }

    override fun getItemCount() = list.size


    inner class ViewHolder(val binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root)

}