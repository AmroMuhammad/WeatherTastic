package com.amro.weathertastic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.databinding.FavouriteItemBinding
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.FavouriteViewModel

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
        holder.binding.weatherItemRoot.setOnLongClickListener {
            viewModel.showDeletionDialog(
                context,
                list[position].lat.toString(),
                list[position].lon.toString()
            )
            true
        }
        holder.binding.cityNameTV.text = viewModel.getCityName(context,savedLang,list[position].lat,list[position].lon,list[position].timezone!!)
    }

    override fun getItemCount() = list.size

    fun setIncomingList(incomingList: List<WeatherResponse>) {
        list.clear()
        list.addAll(incomingList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: FavouriteItemBinding) : RecyclerView.ViewHolder(binding.root)
}