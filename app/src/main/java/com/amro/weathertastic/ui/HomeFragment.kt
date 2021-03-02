package com.amro.weathertastic.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = HomeFragmentBinding.inflate(inflater,container,false)
        getLatestLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //latest location and permission
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.fetchDailyData().observe(this.viewLifecycleOwner, {
            if(it != null)
                binding.textView.text = it.daily?.get(0)?.weather?.get(0)?.description.toString()
        })
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
                    interval = 10000
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
            activity?.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, AppCompatActivity.MODE_PRIVATE)?.edit()?.putBoolean(Constants.SETTINGS_IS_LOCATION_ENABLED,true)?.apply()
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
        editor?.putString(Constants.CURRENT_LATITUDE,latitude)?.apply()
        editor?.putString(Constants.CURRENT_LONGITUDE,longitude)?.apply()
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