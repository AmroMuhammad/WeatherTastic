package com.amro.weathertastic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.WeatherItemBinding
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.viewModel.FavouriteViewModel

class FavouriteRecyclerAdaptor(val list: ArrayList<WeatherResponse>,var viewModel:FavouriteViewModel) : RecyclerView.Adapter<FavouriteRecyclerAdaptor.ViewHolder>() {
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
        holder.binding.weatherItemRoot.setOnLongClickListener(View.OnLongClickListener {
            showDeletionDialog(list[position].lat.toString(),list[position].lon.toString())
            true
        })
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

    inner class ViewHolder(val binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root)
}