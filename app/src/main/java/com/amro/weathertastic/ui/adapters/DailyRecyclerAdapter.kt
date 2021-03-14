package com.amro.weathertastic.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.DailyItemBinding
import com.amro.weathertastic.model.entities.DailyItem
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.HomeViewModel
import java.text.NumberFormat
import java.util.*

class DailyRecyclerAdapter(val list : List<DailyItem?>, private val timezoneOffset: Int,val viewModel: HomeViewModel) : RecyclerView.Adapter<DailyRecyclerAdapter.ViewHolder>() {
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
        holder.binding.cityTimeTV.text = viewModel.getDayTimeDaily(list[position]?.dt!!,timezoneOffset,savedLang)
        araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position]?.windSpeed?.toInt());
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

    override fun getItemCount()= list.size


    inner class ViewHolder (val binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)
}