package com.amro.weathertastic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.databinding.WeatherItemBinding
import com.amro.weathertastic.model.entities.WeatherResponse

class FavouriteRecyclerAdaptor(val list: ArrayList<WeatherResponse>) : RecyclerView.Adapter<FavouriteRecyclerAdaptor.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            WeatherItemBinding.inflate(LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.dateTxt.text = list[position].timezone
    }

    override fun getItemCount() = list.size

    fun setIncomingList(incomingList: List<WeatherResponse>) {
        list.clear()
        list.addAll(incomingList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root)
}