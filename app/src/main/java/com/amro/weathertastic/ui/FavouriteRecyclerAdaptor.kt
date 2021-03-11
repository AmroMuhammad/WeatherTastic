package com.amro.weathertastic.ui

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.FavouriteItemBinding
import com.amro.weathertastic.databinding.WeatherItemBinding
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.FavouriteViewModel
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class FavouriteRecyclerAdaptor(val list: ArrayList<WeatherResponse>,var viewModel:FavouriteViewModel) : RecyclerView.Adapter<FavouriteRecyclerAdaptor.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var savedLang: String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        savedLang = context.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(
            Constants.LANGUAGE, "en").toString()

        return ViewHolder(FavouriteItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding.dateTxt.text = list[position].timezone
        holder.binding.weatherItemRoot.setOnLongClickListener(View.OnLongClickListener {
            showDeletionDialog(list[position].lat.toString(),list[position].lon.toString())
            true
        })
        holder.binding.cityNameTV.text = getCityName(position)
    }

    private fun showDeletionDialog(lat:String,lon: String){
        val builder = AlertDialog.Builder(context).setCancelable(false)
        builder.setTitle(R.string.caution)
        builder.setMessage(R.string.alertMessage1)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            viewModel.deleteFromFavourite(lat,lon)
        }
        builder.setNegativeButton(R.string.no) { _, _ ->
        }
        builder.show()
    }

    override fun getItemCount() = list.size

    fun setIncomingList(incomingList: List<WeatherResponse>) {
        list.clear()
        list.addAll(incomingList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: FavouriteItemBinding) : RecyclerView.ViewHolder(binding.root)

    private fun getCityName(position: Int):String{
        var locationAddress = ""
        val geocoder = Geocoder(context, Locale(savedLang));
        try {
            if(savedLang=="ar"){
                locationAddress = geocoder.getFromLocation(list[position].lat,list[position].lon,1)[0].countryName ?: list[position].timezone!!
            }else{
                locationAddress = geocoder.getFromLocation(list[position].lat,list[position].lon,1)[0].adminArea ?: list[position].timezone!!
                locationAddress += ", ${geocoder.getFromLocation(list[position].lat,list[position].lon,1)[0].countryName ?: list[position].timezone!!}"}
        } catch (e: IOException){
            e.printStackTrace()
        }
        return locationAddress
    }
}