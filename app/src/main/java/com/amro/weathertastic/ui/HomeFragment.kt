package com.amro.weathertastic.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.amro.weathertastic.viewModel.HomeViewModel
import com.amro.weathertastic.databinding.HomeFragmentBinding
import com.amro.weathertastic.utils.Constants
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.math.BigDecimal
import java.math.RoundingMode

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private var hourlyAdapter = HourlyRecyclerAdapter(ArrayList())
    private var dailyAdapter = DailyRecyclerAdapter(ArrayList())


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = HomeFragmentBinding.inflate(inflater,container,false)
        //latest location and permission
        getLatestLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initRecyclers()
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.fetchDailyData().observe(viewLifecycleOwner, {
            if(it != null && it.timezone != null)
                binding.mainCard.dateTxt.text = it.timezone
                //hourlyAdapter.setIncomingList(it.hourly!!)
                //dailyAdapter.setIncomingList(it.daily!!)
        })
    }

    fun initRecyclers(){
        binding.hourlyRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.dailyRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.hourlyRecycler.adapter = hourlyAdapter
        binding.dailyRecycler.adapter = dailyAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //request Location Permission
    @SuppressLint("MissingPermission")
    fun getLatestLocation() {
        if (isPermissionGranted()) {
            if (checkLocation()) {
                val locationRequest = LocationRequest()
                with(locationRequest) {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 1000
                }
                val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            } else {
                val isLocationEnabled = activity?.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, AppCompatActivity.MODE_PRIVATE)?.getBoolean(Constants.SETTINGS_IS_LOCATION_ENABLED,false)
                if(!isLocationEnabled!!)
                    showErrorDialog("No Location Error","Kindly enable Location to use Application properly")
            }
        } else {
            requestPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocation(): Boolean {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            activity?.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, AppCompatActivity.MODE_PRIVATE)?.edit()?.putBoolean(Constants.SETTINGS_IS_LOCATION_ENABLED,true)?.commit()
            return true
        }else{
            return false;
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            // TODO use current location long and lat
            val lonDecimal = BigDecimal(location.longitude).setScale(4, RoundingMode.HALF_DOWN)
            val latDecimal = BigDecimal(location.latitude).setScale(4, RoundingMode.HALF_DOWN)
            //Toast.makeText(requireContext(), "Latitude:${latDecimal}\nlongitude:${lonDecimal}", Toast.LENGTH_SHORT).show()
            saveCurrentLocationToSharedPref(latDecimal.toString(),lonDecimal.toString())
        }
    }

    private fun saveCurrentLocationToSharedPref(latitude: String,longitude: String){
        val sharedPref = activity?.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        if(sharedPref?.getString(Constants.CURRENT_LONGITUDE,"null") != longitude){
            editor?.putString(Constants.OLD_LATITUDE,sharedPref.getString(Constants.CURRENT_LATITUDE,"null"))?.apply()
            editor?.putString(Constants.OLD_LONGITUDE,sharedPref.getString(Constants.CURRENT_LONGITUDE,"null"))?.apply()
            editor?.putString(Constants.CURRENT_LATITUDE,latitude)?.apply()
            editor?.putString(Constants.CURRENT_LONGITUDE,longitude)?.apply()
        }
    }

    private fun enableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), Constants.LOCATION_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLatestLocation()
        }
    }

    private fun showErrorDialog( title:String, message:String){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton("Get Permission") { _, _ ->
            enableLocation()
        }
        builder.setNegativeButton("Ignore") { _, _ ->
        }
        builder.show()
    }

}