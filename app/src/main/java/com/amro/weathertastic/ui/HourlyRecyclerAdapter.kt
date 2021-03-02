package com.amro.weathertastic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.databinding.WeatherItemBinding
import com.amro.weathertastic.model.entities.DailyItem
import com.amro.weathertastic.model.entities.HourlyItem

class HourlyRecyclerAdapter(var list: ArrayList<HourlyItem?>) : RecyclerView.Adapter<HourlyRecyclerAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(WeatherItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.dateTxt.text = list[position]?.weather?.get(0)?.description.toString()
    }

    override fun getItemCount() = list.size

    fun setIncomingList(incomingList: List<HourlyItem?>){
        list.clear()
        list.addAll(incomingList)
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root)

}