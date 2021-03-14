package com.amro.weathertastic.ui.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.ActivityMainBinding
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel: MainViewModel
//    private val
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    viewModel.setAppLocale(getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).getString(Constants.LANGUAGE,"en")!!,this@MainActivity)
        binding = ActivityMainBinding.inflate(layoutInflater)

    //initialize viewModel

    //hide status bar
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(binding.root)

        //inflate toolbar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.favouriteFragment, R.id.alarmFragment))

        //inflate and connect Bottom Navigation view to Nav Comp
        binding.bottomNav.setupWithNavController(navController)

        if(viewModel.isFirstTime(this@MainActivity)){
            viewModel.createSettingsSharedPreferences(this@MainActivity)
            if(viewModel.isOnline(this)){
                getSharedPreferences(Constants.SHARED_PREF_SETTINGS, Context.MODE_PRIVATE).edit().putBoolean(Constants.SETTINGS_IS_FIRST_TIME,false).apply()
            }else{
                showErrorDialog(this)
            }
        } else{
        }
        if(viewModel.dayOrNight(viewModel.getDayTime(),"06:00","17:00") == "day" ){
            binding.starsWhite.onStop()
            binding.stars.onStop()
            binding.stars.visibility = View.GONE
            binding.starsWhite.visibility = View.GONE
            binding.gifBG.visibility = View.VISIBLE
            Log.i(Constants.LOG_TAG, "day")
        }else{
            binding.starsWhite.onStart()
            binding.stars.onStart()
            binding.stars.visibility = View.VISIBLE
            binding.starsWhite.visibility = View.VISIBLE
            binding.gifBG.visibility = View.GONE
            Log.i(Constants.LOG_TAG, "night")
        }

    }

    private fun showErrorDialog(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.NetworkError)
        builder.setMessage("Kindly enable internet for first time to launch properly").setCancelable(false)
        builder.setPositiveButton(R.string.Exit) { _, _ ->
            finish()
        }
        builder.show()
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
        }else{  super.onOptionsItemSelected(item)
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
//                        Toast.makeText(this@MainActivity,list[p1],Toast.LENGTH_SHORT).show()
                    newLang = if(list[p1] == "English")"en" else "ar"
                }
            })
            setPositiveButton(R.string.save,object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    sharedPref.edit().putString(Constants.LANGUAGE,newLang).apply()
                    viewModel.setAppLocale(newLang,this@MainActivity)
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
        var newUnit = "metric"
        val list = arrayOf("metric","imperial")
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF_SETTINGS, MODE_PRIVATE)
        val lastUnit:String = if(sharedPref.getString(Constants.UNITS,"metric") == "metric"){ "metric" }else{"imperial"}
        val selectedUnitNum = list.indexOf(lastUnit)
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle(R.string.units)
            setSingleChoiceItems(list,selectedUnitNum,object: DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
//                    Toast.makeText(this@MainActivity,list[p1],Toast.LENGTH_SHORT).show()
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

 /*   private fun setAppLocale(localeCode: String){
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.getConfiguration()
        config.setLocale(Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }*/


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }



}