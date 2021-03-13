    package com.amro.weathertastic.ui

import android.content.Context
import android.location.Geocoder
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.utils.SunriseViewLibrary.SimpleSunriseSunsetLabelFormatter
import com.amro.weathertastic.utils.SunriseViewLibrary.Time
import com.amro.weathertastic.databinding.ItemPageBinding
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.HomeViewModel
import com.github.matteobattilana.weather.PrecipType
import com.jem.fliptabs.FlipTab
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

    class ViewPagerAdapter(private var list:ArrayList<WeatherResponse>,val viewModel: HomeViewModel) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
        private lateinit var context: Context
        private lateinit var savedUnit: String
        private lateinit var savedLang: String


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewPagerAdapter.ViewHolder {
            context = parent.context
            savedUnit = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.UNITS, "metric").toString()
            savedLang = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.LANGUAGE, "en").toString()
            val view =
                ViewHolder(ItemPageBinding.inflate(LayoutInflater.from(context), parent, false))
            return view
        }

        override fun onBindViewHolder(holder: ViewPagerAdapter.ViewHolder, position: Int) {
            //setting two recyclers
            initRecyclers(holder)
            holder.binding.hourlyRecycler.adapter = HourlyRecyclerAdapter(list[position].hourly!!,list[position].timezoneOffset!!,viewModel)
            holder.binding.dailyRecycler.adapter = DailyRecyclerAdapter(list[position].daily!!,list[position].timezoneOffset!!,viewModel)

            //assigning values
            holder.binding.mainCard.cityNameTV.text = viewModel.getCityName(context,savedLang,list[position].lat,list[position].lon,list[position].timezone!!)
            holder.binding.mainCard.condTV.text = list[position].current!!.weather?.get(0)!!.description!!
            holder.binding.mainCard.cityTimeDayTV.text = viewModel.getDayTime(list[position].current?.dt!!,list[position].timezoneOffset!!,savedLang)
            val araNum = NumberFormat.getInstance(Locale(savedLang)).format(list[position].current!!.temp);
            holder.binding.mainCard.degreeMin.text = araNum.toString()
            holder.binding.mainCard.condImg.setAnimation(
                viewModel.getIcon(
                    list[position].current?.weather?.get(
                        0
                    )?.icon!!
                )
            )
            if (viewModel.dayOrNight(viewModel.getDayTime(list[position].current?.dt!!,list[position].timezoneOffset!!,savedLang)
                    ,viewModel.sunRiseTime(list[position].current?.sunrise!!,list[position].timezoneOffset!!,savedLang)
                    ,viewModel.sunsetTime(list[position].current?.sunset!!,list[position].timezoneOffset!!,savedLang)) == "day"
            ) {
                holder.binding.stars.visibility = View.GONE
                holder.binding.starsWhite.visibility = View.GONE
//                holder.binding.HomeConstraintLayout.background = context.resources.getDrawable(R.drawable.gradient_day)
                holder.binding.gifBG.visibility = View.VISIBLE
//                holder.binding.seprator1.setTextColor(context.resources.getColor(R.color.sepratorDay))
//                holder.binding.seprator2.setTextColor(context.resources.getColor(R.color.sepratorDay))
                holder.binding.seprator3.setTextColor(context.resources.getColor(R.color.sepratorDay))
                holder.binding.seprator4.setTextColor(context.resources.getColor(R.color.sepratorDay))
                holder.binding.flibTabSwitch.setOverallColor(context.resources.getColor(R.color.sepratorDay))
                holder.binding.windview.textColor = context.resources.getColor(R.color.sepratorDay)

            } else {
                holder.binding.starsWhite.onStart()
                holder.binding.stars.onStart()
                holder.binding.stars.visibility = View.VISIBLE
                holder.binding.starsWhite.visibility = View.VISIBLE
                holder.binding.HomeConstraintLayout.background = context.resources.getDrawable(R.drawable.gradient_sky)
                holder.binding.gifBG.visibility = View.GONE
//                holder.binding.seprator1.setTextColor(context.resources.getColor(R.color.sepratorNight))
//                holder.binding.seprator2.setTextColor(context.resources.getColor(R.color.sepratorNight))
                holder.binding.seprator3.setTextColor(context.resources.getColor(R.color.sepratorNight))
                holder.binding.seprator4.setTextColor(context.resources.getColor(R.color.sepratorNight))
                holder.binding.flibTabSwitch.setOverallColor(context.resources.getColor(R.color.sepratorNight))
                holder.binding.windview.textColor = context.resources.getColor(R.color.sepratorNight)

            }

            if (savedUnit == "metric") {
                //wind and pressure
                holder.binding.windview.pressure = list[position].current?.pressure?.toFloat()!!
                holder.binding.windview.pressureUnit = "in hPa"
                holder.binding.windview.setWindSpeed(list[position].current?.windSpeed?.toFloat()!!)
                holder.binding.windview.windSpeedUnit = " meter/sec"
                holder.binding.windview.start()
                holder.binding.mainCard.tempDegreeTV.setImageResource(R.drawable.ic_celsius)
            } else {
                holder.binding.windview.pressure = list[position].current?.pressure?.toFloat()!!
                holder.binding.windview.pressureUnit = "in hPa"
                holder.binding.windview.setWindSpeed(list[position].current?.windSpeed?.toFloat()!!)
                holder.binding.windview.windSpeedUnit = " miles/hour"
                holder.binding.windview.start()
                holder.binding.mainCard.tempDegreeTV.setImageResource(R.drawable.ic_fahrenheit)
            }

            //sunrise and sunset
            holder.binding.ssv.sunriseTime = Time(viewModel.sunRiseTime(list[position].current?.sunrise!!,list[position].timezoneOffset!!,savedLang).substringBefore(":").toInt()
                ,viewModel.sunRiseTime(list[position].current?.sunrise!!,list[position].timezoneOffset!!,savedLang).substringAfter(":").toInt());
            holder.binding.ssv.sunsetTime = Time(viewModel.sunsetTime(list[position].current?.sunset!!,list[position].timezoneOffset!!,savedLang).substringBefore(":").toInt()
                ,viewModel.sunsetTime(list[position].current?.sunset!!,list[position].timezoneOffset!!,savedLang).substringAfter(":").toInt())
            holder.binding.ssv.labelFormatter = object : SimpleSunriseSunsetLabelFormatter() {
                private fun formatLabel(time: Time): String {
                    return String.format(
                        Locale(savedLang),
                        "%02dh %02dm",
                        time.hour,
                        time.minute
                    );
                }

                override fun formatSunriseLabel(sunrise: Time): String {
                    return formatLabel(sunrise)
                }

                override fun formatSunsetLabel(sunset: Time): String {
                    return formatLabel(sunset)
                }
            }
            holder.binding.ssv.startAnimate(viewModel.getTimeInCalender(list[position].current?.dt!!,list[position].timezoneOffset!!))

            //rain or snow
            when (list[position].current?.weather?.get(0)?.main) {
                "Rain" -> {
                    holder.binding.weatherView.visibility = View.VISIBLE
                    holder.binding.weatherView.setWeatherData(PrecipType.RAIN)
                    holder.binding.weatherView.angle = 20
                    holder.binding.weatherView.emissionRate = 500.0F
                    holder.binding.weatherView.fadeOutPercent = 5f
                }
                "Snow" -> {
                    holder.binding.weatherView.visibility = View.VISIBLE
                    holder.binding.weatherView.setWeatherData(PrecipType.SNOW)
                    holder.binding.weatherView.angle = 20
                    holder.binding.weatherView.emissionRate = 200.0F
                    holder.binding.weatherView.fadeOutPercent = 1f
                }
                "Drizzle" -> {
                    holder.binding.weatherView.visibility = View.VISIBLE
                    holder.binding.weatherView.setWeatherData(PrecipType.RAIN)
                    holder.binding.weatherView.angle = 20
                    holder.binding.weatherView.emissionRate = 100.0F
                    holder.binding.weatherView.fadeOutPercent = 0.7f
                }
                else -> {
                    holder.binding.weatherView.visibility = View.GONE
                }
            }

            //flipswitch
            holder.binding.flibTabSwitch.setTabSelectedListener(object : FlipTab.TabSelectedListener{
                override fun onTabReselected(isLeftTab: Boolean, tabTextValue: String) {
                    if(isLeftTab){
                        holder.binding.hourlyRecycler.visibility = View.VISIBLE
                        holder.binding.dailyRecycler.visibility = View.GONE
                    }
                    else{
                        holder.binding.hourlyRecycler.visibility = View.GONE
                        holder.binding.dailyRecycler.visibility = View.VISIBLE
                    }
                }

                override fun onTabSelected(isLeftTab: Boolean, tabTextValue: String) {
                    if(isLeftTab){
                        holder.binding.hourlyRecycler.visibility = View.VISIBLE
                        holder.binding.dailyRecycler.visibility = View.GONE
                    }
                    else{
                        holder.binding.hourlyRecycler.visibility = View.GONE
                        holder.binding.dailyRecycler.visibility = View.VISIBLE
                    }
                }
            })
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
}
