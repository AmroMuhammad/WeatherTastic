    package com.amro.weathertastic.ui

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.ItemPageBinding
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.utils.Constants
import com.github.tianma8023.formatter.SunriseSunsetLabelFormatter
import com.github.tianma8023.model.Time
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

    class ViewPagerAdapter(private var list:ArrayList<WeatherResponse>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var savedUnit:String


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapter.ViewHolder {
        context = parent.context
            savedUnit = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS,Context.MODE_PRIVATE).getString(Constants.UNITS,"metric").toString()
        val view = ViewHolder(ItemPageBinding.inflate(LayoutInflater.from(context), parent, false))
            return view
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.ViewHolder, position: Int) {
        //setting two recyclers
        initRecyclers(holder)
        holder.binding.hourlyRecycler.adapter = HourlyRecyclerAdapter(list[position].hourly!!)
        holder.binding.dailyRecycler.adapter = DailyRecyclerAdapter(list[position].daily!!)

        //assigning values
        holder.binding.mainCard.cityNameTV.text = getCityName(position)
        holder.binding.mainCard.condTV.text = list[position].current!!.weather?.get(0)!!.main!!
        holder.binding.mainCard.cityTimeDayTV.text = getDayTime(position)
        holder.binding.mainCard.degreeMin.text = list[position].current!!.temp.toString()
        holder.binding.mainCard.condImg.setAnimation(getIcon(list[position].current?.weather?.get(0)?.icon!!))
        if(dayOrNight(getDayTime(position),sunRiseTime(position),sunsetTime(position)) == "day"){
            holder.binding.starsWhite.onStop()
            holder.binding.stars.onStop()
            holder.binding.stars.visibility = View.GONE
            holder.binding.starsWhite.visibility = View.GONE
            holder.binding.HomeConstraintLayout.background = context.resources.getDrawable(R.drawable.gradient_day)
            holder.binding.seprator1.setTextColor(context.resources.getColor(R.color.sepratorDay))
            holder.binding.seprator2.setTextColor(context.resources.getColor(R.color.sepratorDay))
            holder.binding.seprator3.setTextColor(context.resources.getColor(R.color.sepratorDay))
            holder.binding.seprator4.setTextColor(context.resources.getColor(R.color.sepratorDay))



        }else{
            holder.binding.starsWhite.onStart()
            holder.binding.stars.onStart()
            holder.binding.stars.visibility = View.VISIBLE
            holder.binding.starsWhite.visibility = View.VISIBLE
            holder.binding.HomeConstraintLayout.background = context.resources.getDrawable(R.drawable.gradient_sky)
            holder.binding.seprator1.setTextColor(context.resources.getColor(R.color.sepratorNight))
            holder.binding.seprator2.setTextColor(context.resources.getColor(R.color.sepratorNight))
            holder.binding.seprator3.setTextColor(context.resources.getColor(R.color.sepratorNight))
            holder.binding.seprator4.setTextColor(context.resources.getColor(R.color.sepratorNight))

        }

        if(savedUnit == "metric"){
            //wind and pressure
            holder.binding.windview.pressure = list[position].current?.pressure?.toFloat()!!
            holder.binding.windview.pressureUnit = "in hPa"
            holder.binding.windview.setWindSpeed(list[position].current?.windSpeed?.toFloat()!!)
            holder.binding.windview.windSpeedUnit = " meter/sec"
            holder.binding.windview.start()
            holder.binding.mainCard.tempDegreeTV.setImageResource(R.drawable.ic_celsius)
        }else{
            holder.binding.windview.pressure = list[position].current?.pressure?.toFloat()!!
            holder.binding.windview.pressureUnit = "in hPa"
            holder.binding.windview.setWindSpeed(list[position].current?.windSpeed?.toFloat()!!)
            holder.binding.windview.windSpeedUnit = " miles/hour"
            holder.binding.windview.start()
            holder.binding.mainCard.tempDegreeTV.setImageResource(R.drawable.ic_fahrenheit)
        }

        holder.binding.ssv.sunriseTime = Time(sunRiseTime(position).substringBefore(":").toInt(), sunRiseTime(position).substringAfter(":").toInt());
        holder.binding.ssv.sunsetTime = Time(sunsetTime(position).substringBefore(":").toInt(), sunsetTime(position).substringAfter(":").toInt())
        holder.binding.ssv.labelFormatter = object: SunriseSunsetLabelFormatter {
            private fun formatLabel(time: Time):String {
                return String.format(Locale.getDefault(), "%02dh %02dm", time.hour, time.minute);
            }

            override fun formatSunriseLabel(sunrise: Time): String {
                return formatLabel(sunrise);
            }

            override fun formatSunsetLabel(sunset: Time): String {
                return formatLabel(sunset);
            }
        };
        holder.binding.ssv.startAnimate(setCalnder(position))

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
            holder.binding.dailyRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            holder.binding.hourlyRecycler.setHasFixedSize(true)
            holder.binding.dailyRecycler.setHasFixedSize(true)
        }

        private fun getCityName(position: Int):String{
            var locationAddress = ""
            val geocoder = Geocoder(context);
            try {
                locationAddress = geocoder.getFromLocation(list[position].lat,list[position].lon,1)[0].adminArea ?: list[position].timezone!!
                locationAddress += ", ${geocoder.getFromLocation(list[position].lat,list[position].lon,1)[0].countryName ?: list[position].timezone!!}"
            } catch (e: IOException){
                e.printStackTrace()
            }
            return locationAddress
        }

        private fun getDayTime(position: Int):String{
            val calender = Calendar.getInstance()
            calender.timeInMillis = (list[position].current?.dt?.plus(list[position].timezoneOffset!!)?.minus(7200)?.toLong() ?: 10)*1000L
            val dateFormat = SimpleDateFormat("EE, HH:MM");
            return dateFormat.format(calender.time)
        }

        private fun sunRiseTime(position: Int):String{
            val calender = Calendar.getInstance()
            calender.timeInMillis = (list[position].current?.sunrise?.plus(list[position].timezoneOffset!!)?.minus(7200)?.toLong() ?: 10)*1000L
            val dateFormat = SimpleDateFormat("HH:MM");
            return dateFormat.format(calender.time)
        }

        private fun sunsetTime(position: Int):String{
            val calender = Calendar.getInstance()
            calender.timeInMillis = (list[position].current?.sunset?.plus(list[position].timezoneOffset!!)?.minus(7200)?.toLong() ?: 10)*1000L
            val dateFormat = SimpleDateFormat("HH:MM");
            return dateFormat.format(calender.time)
        }

        private fun dayOrNight(currentTime: String,sunrise:String,sunset:String): String {
            val currentNum = currentTime.substringAfter(" ").substringBefore(":")
            val sunriseNum = sunrise.substringBefore(":")
            val sunsetNum = sunset.substringBefore(":")
            if(currentNum in sunriseNum..sunsetNum){
                return "day"
            }else{
                return "night"
            }
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

        private fun setCalnder(position: Int):Calendar{
            val calendere = Calendar.getInstance()
            calendere.timeInMillis = (list[position].current?.dt?.plus(list[position].timezoneOffset!!)?.minus(7200)?.toLong() ?: 10)*1000L
            return calendere
        }

}
