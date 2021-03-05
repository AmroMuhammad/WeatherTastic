    package com.amro.weathertastic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.databinding.ItemPageBinding
import com.amro.weathertastic.model.entities.WeatherResponse

    class ViewPagerAdapter(private var list:ArrayList<WeatherResponse>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    private lateinit var context: Context
    private var hourlyAdapter = HourlyRecyclerAdapter(ArrayList())
    private var dailyAdapter = DailyRecyclerAdapter(ArrayList())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapter.ViewHolder {
        context = parent.context
        val view = ViewHolder(ItemPageBinding.inflate(LayoutInflater.from(context), parent, false))
        view.initRecyclers()
        return view
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.ViewHolder, position: Int) {
        //holder.initRecyclers()
        holder.changeLists(position)
        holder.binding.mainCard.dateTxt.text = list[position].timezone
        holder.binding.mainCard.degreeMax.text = list[position].current?.feelsLike.toString()

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setIncomingList(incomingList: List<WeatherResponse>){
        list.clear()
        list.addAll(incomingList)
        notifyDataSetChanged()
    }



    inner class ViewHolder(val binding: ItemPageBinding) : RecyclerView.ViewHolder(binding.root){

        fun initRecyclers(){
            binding.hourlyRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            binding.dailyRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            binding.hourlyRecycler.adapter = hourlyAdapter
            binding.dailyRecycler.adapter = dailyAdapter
        }

        fun changeLists(position:Int){
            hourlyAdapter.setIncomingList(list[position].hourly!!)
            dailyAdapter.setIncomingList(list[position].daily!!)
        }
    }


}
