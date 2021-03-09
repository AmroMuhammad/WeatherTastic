    package com.amro.weathertastic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.databinding.ItemPageBinding
import com.amro.weathertastic.model.entities.HourlyItem
import com.amro.weathertastic.model.entities.WeatherResponse

    class ViewPagerAdapter(private var list:ArrayList<WeatherResponse>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapter.ViewHolder {
        context = parent.context
        val view = ViewHolder(ItemPageBinding.inflate(LayoutInflater.from(context), parent, false))
        return view
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.ViewHolder, position: Int) {
        initRecyclers(holder)
        holder.binding.mainCard.dateTxt.text = list[position].timezone
        holder.binding.mainCard.degreeMax.text = list[position].current?.feelsLike.toString()
//        changeLists(position)
        holder.binding.hourlyRecycler.adapter = HourlyRecyclerAdapter(list[position].hourly!!)
        holder.binding.dailyRecycler.adapter = DailyRecyclerAdapter(list[position].daily!!)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setIncomingList(incomingList: List<WeatherResponse>){
        list.clear()
        list.addAll(incomingList)
        notifyDataSetChanged()
    }



    inner class ViewHolder(val binding: ItemPageBinding) : RecyclerView.ViewHolder(binding.root)

        fun initRecyclers(holder:ViewHolder){
            holder.binding.hourlyRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            holder.binding.dailyRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            holder.binding.hourlyRecycler.setHasFixedSize(true)
            holder.binding.dailyRecycler.setHasFixedSize(true)
        }


}
