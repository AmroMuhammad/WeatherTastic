package com.amro.weathertastic.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.ActivityMainBinding
import com.amro.weathertastic.utils.Constants
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inflate toolbar
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.favouriteFragment,
                R.id.alarmFragment
            )
        )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //inflate and connect Bottom Navigation view to Nav Comp
        binding.bottomNav.setupWithNavController(navController)

    }

    override fun onStart() {
        super.onStart()
        //latest location and permission
        getLatestLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.aboutUsMenuItem) {
            // TODO: added to ViewModel
            Toast.makeText(applicationContext, "toast message with gravity", Toast.LENGTH_SHORT)
                .show()
            true
        } else {
            item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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
                    setNumUpdates(2)
                }
                val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            } else {
                enableLocation()
            }
        } else {
            requestPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocation(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            // TODO use current location long and lat
            Toast.makeText(this@MainActivity, "Latitude:${location.latitude}\nlongitude:${location.longitude}", Toast.LENGTH_LONG).show()
            saveCurrentLocationToSharedPref(location.latitude.toString(),location.longitude.toString())
        }
    }

    private fun saveCurrentLocationToSharedPref(latitude: String,longitude: String){
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.CURRENT_LATITUDE,latitude).apply()
        editor.putString(Constants.CURRENT_LONGITUDE,longitude).apply()
    }

    private fun enableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), Constants.LOCATION_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLatestLocation()
        }
    }
}