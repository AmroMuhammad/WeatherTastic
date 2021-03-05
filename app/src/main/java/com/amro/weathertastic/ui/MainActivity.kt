package com.amro.weathertastic.ui

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
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
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.LANGUAGE,"en")!!)
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
            createSettingsSharedPreferences()
            if(isOnline(this)){
                getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).edit().putBoolean(Constants.SETTINGS_IS_FIRST_TIME,false).apply()
            }else{
                showErrorDialog(this)
            }
        }
        else{
        }
    }

    private fun showErrorDialog(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.NetworkError)
        builder.setMessage("Kindly enable internet for first time to launch properly")

        builder.setPositiveButton(R.string.Exit) { _, _ ->
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
        return if (item.itemId == R.id.changeLanguageMenuItem) {
            showLanguageOptionsDialog()
            true
        } else if(item.itemId == R.id.changeUnitsMenuItem) {
            showUnitsOptionsDialog()
            true
        }else{ item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }

    private fun showLanguageOptionsDialog() {
        var newLang = "en"
        val list = arrayOf("English","العربية")
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF_SETTINGS, MODE_PRIVATE)
        val lastLang:String = if(sharedPref.getString(Constants.LANGUAGE,"en") == "en"){ "English" }else {"العربية"}
        val selectedLangNum = list.indexOf(lastLang)
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle(R.string.language)
            setSingleChoiceItems(list,selectedLangNum,object: DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                        Toast.makeText(this@MainActivity,list[p1],Toast.LENGTH_SHORT).show()
                    newLang = if(list[p1] == "English")"en" else "ar"
                }
            })
            setPositiveButton(R.string.save,object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    sharedPref.edit().putString(Constants.LANGUAGE,newLang).apply()
                    setAppLocale(newLang)
                    val intent = getIntent()
                    finish()
                    startActivity(intent)
                }
            })
            setNegativeButton(R.string.cancel,object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
                }
            })
            show()
        }
    }

    private fun showUnitsOptionsDialog() {
        var newUnit = "default"
        val list = arrayOf("default","metric","imperial")
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF_SETTINGS, MODE_PRIVATE)
        val lastUnit:String = if(sharedPref.getString(Constants.UNITS,"default") == "default"){ "default" }else if(sharedPref.getString(Constants.UNITS,"default") == "metric"){"metric"}else{"imperial"}
        val selectedUnitNum = list.indexOf(lastUnit)
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle(R.string.units)
            setSingleChoiceItems(list,selectedUnitNum,object: DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    Toast.makeText(this@MainActivity,list[p1],Toast.LENGTH_SHORT).show()
                    newUnit = list[p1]
                }
            })
            setPositiveButton(R.string.save,object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    sharedPref.edit().putString(Constants.UNITS,newUnit).apply()
                    val intent = getIntent()
                    finish()
                    startActivity(intent)
                }
            })
            setNegativeButton(R.string.cancel,object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
                }
            })
            show()
        }
    }

    private fun setAppLocale(localeCode: String){
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.getConfiguration()
        config.setLocale(Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun createSettingsSharedPreferences(){
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF_SETTINGS, MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putString(Constants.LANGUAGE,Constants.LANGUAGE_VALUE)?.apply()
        editor?.putString(Constants.UNITS,Constants.UNITS_VALUE)?.apply()
    }


}