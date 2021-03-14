package com.amro.weathertastic.ui.activities

import android.content.Intent
import com.amro.weathertastic.R
import com.daimajia.androidanimations.library.Techniques
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.cnst.Flags
import com.viksaa.sssplash.lib.model.ConfigSplash


//extends AwesomeSplash!
class SplashActivity : AwesomeSplash() {
    //DO NOT OVERRIDE onCreate()!
    //if you need to start some services do it in initSplash()!
    override fun initSplash(configSplash: ConfigSplash) {

        /* you don't have to override every property */

        //Customize Circular Reveal
        configSplash.backgroundColor = R.color.dayPressed //any color you want form colors.xml
        configSplash.animCircularRevealDuration = 2000 //int ms
        configSplash.revealFlagX = Flags.REVEAL_RIGHT //or Flags.REVEAL_LEFT
        configSplash.revealFlagY = Flags.REVEAL_BOTTOM //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.logoSplash = R.drawable.splash //or any other drawable
        configSplash.animLogoSplashDuration = 2000 //int ms
        configSplash.animLogoSplashTechnique = Techniques.BounceIn //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)



        //Customize Title
        configSplash.titleSplash = "WeatherTastic"
        configSplash.titleTextColor = R.color.greyCard
        configSplash.titleTextSize = 50f //float value
        configSplash.animTitleDuration = 3000
        configSplash.animTitleTechnique = Techniques.DropOut
        configSplash.titleFont = "fonts/tt.ttf" //provide string to your font located in assets/fonts/
    }

    override fun animationsFinished() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}