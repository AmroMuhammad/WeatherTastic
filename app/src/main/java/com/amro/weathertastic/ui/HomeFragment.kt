package com.amro.weathertastic.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.HomeFragmentBinding
import com.amro.weathertastic.model.entities.WeatherResponse
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.HomeViewModel
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
    private lateinit var viewPagerAdapter :ViewPagerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = HomeFragmentBinding.inflate(inflater,container,false)
        //latest location and permission
        getLatestLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initialize ViewPager
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextAppearance(context,R.style.SanchezTextAppearance)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewPagerAdapter = ViewPagerAdapter(ArrayList(),viewModel)
        binding.viewPager2.adapter = viewPagerAdapter

        viewModel.loadAllData().observe(viewLifecycleOwner, {
            if(it != null ) {
                if(it.isNotEmpty()) {
                    binding.progressbar.visibility = View.GONE;
                    Log.i(Constants.LOG_TAG, "Here")
                    viewPagerAdapter.setIncomingList(it.reversed())
                    binding.indicator.setViewPager2(binding.viewPager2)
                    binding.backgroundNoData.visibility = View.GONE
                }else{
                binding.backgroundNoData.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.VISIBLE
                }
            }else{
                binding.backgroundNoData.visibility = View.VISIBLE
                binding.progressbar.visibility = View.VISIBLE
            }
        })

        binding.homeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshFav()
            binding.homeRefreshLayout.isRefreshing = false
        })
    }


    fun refreshFav(){
        var list = viewModel.getUnrefreshedData()
        if(list != null){
            for(element in list){
                viewModel.refreshFavData(element.lat.toString(),element.lon.toString())
                Log.i(Constants.LOG_TAG,element.timezone.toString())
            }
        }else{
            Log.i(Constants.LOG_TAG,"not updated")
        }
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
                    numUpdates = 10
                }
                val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            } else {
                val isLocationEnabled = activity?.getSharedPreferences(Constants.SHARED_PREF_SETTINGS, AppCompatActivity.MODE_PRIVATE)?.getBoolean(Constants.SETTINGS_IS_LOCATION_ENABLED,false)
                if(!isLocationEnabled!!)
                    showErrorDialog()
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
            val lonDecimal = BigDecimal(location.longitude).setScale(4, RoundingMode.HALF_DOWN)
            val latDecimal = BigDecimal(location.latitude).setScale(4, RoundingMode.HALF_DOWN)
            saveCurrentLocationToSharedPref(latDecimal.toString(),lonDecimal.toString())
        }
    }

    fun saveCurrentLocationToSharedPref(latitude: String,longitude: String){
        val sharedPref = activity?.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        if(sharedPref?.getString(Constants.CURRENT_LONGITUDE,"null") != longitude){
            editor?.putString(Constants.OLD_LATITUDE,sharedPref.getString(Constants.CURRENT_LATITUDE,"null"))?.apply()
            editor?.putString(Constants.OLD_LONGITUDE,sharedPref.getString(Constants.CURRENT_LONGITUDE,"null"))?.apply()
            editor?.putString(Constants.CURRENT_LATITUDE,latitude)?.apply()
            editor?.putString(Constants.CURRENT_LONGITUDE,longitude)?.apply()
            viewModel.refreshCurrentLocation(latitude,longitude)
        }
    }

    private fun enableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun requestPermission(){
        var permissions= arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permissions, Constants.LOCATION_PERMISSION_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLatestLocation()
            }
        }
    }

    private fun showErrorDialog(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.errorLocation)
        builder.setMessage(R.string.locationMessage)

        builder.setPositiveButton(R.string.getPermission) { _, _ ->
            enableLocation()
        }
        builder.setNegativeButton(R.string.ignore) { _, _ ->
        }
        builder.show()
    }

}