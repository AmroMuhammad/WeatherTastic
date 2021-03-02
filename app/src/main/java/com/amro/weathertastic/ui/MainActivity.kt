package com.amro.weathertastic.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inflate toolbar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.favouriteFragment, R.id.alarmFragment))
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //inflate and connect Bottom Navigation view to Nav Comp
        binding.bottomNav.setupWithNavController(navController)

        if(isFirstTime()){
            Log.i(Constants.LOG_TAG,"first time")
            if(isOnline(this)){
                getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).edit().putBoolean(Constants.SETTINGS_IS_FIRST_TIME,false).apply()
            }else{
                showErrorDialog(this)
            }
        }
        else{
            Log.i(Constants.LOG_TAG,"not first time")
        }

    }

    private fun showErrorDialog(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Network Error")
        builder.setMessage("Kindly enable internet for first time to launch properly")

        builder.setPositiveButton("Exit") { _, _ ->
            finish()
        }

        builder.show()
    }

    private fun isFirstTime():Boolean{
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF_SETTINGS, MODE_PRIVATE)
        val editor = sharedPref?.edit()
        return if(! sharedPref?.contains(Constants.SETTINGS_IS_FIRST_TIME)!!){
            editor?.putBoolean(Constants.SETTINGS_IS_FIRST_TIME,true)?.apply()
            true
        }else{
            false
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i(Constants.LOG_TAG, "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i(Constants.LOG_TAG, "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i(Constants.LOG_TAG, "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
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


}