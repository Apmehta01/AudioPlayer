package com.example.demoaudioplayer.view.activity.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.demoaudioplayer.BuildConfig
import com.example.demoaudioplayer.R
import com.example.demoaudioplayer.view.activity.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showVersionDetails()
        runTimer(2200)
    }

    /**
     *Name: SplashActivity showVersionDetails()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will show the version details into textview.
     **/
    private fun showVersionDetails() {
        val context:Context=this@SplashActivity
        val versionCode: Int = BuildConfig.VERSION_CODE
        val versionName: String = BuildConfig.VERSION_NAME
        txtVersionDetails!!.text=context.resources.getString(R.string.str_version)+" "+versionName.toString()

    }

    /**
     * Name: [SplashActivity] runTimer()
     * <br> Created By Arpan on 06/02/2022
     * <br> Modified By Arpan on 06/02/2022
     * <br> Purpose: Run timer for 2200 millisecond.
     *
     * @param delay delay time in millisecond.
     */
    private fun runTimer(delay: Int) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, delay.toLong())
    }
    override fun onDestroy() {
        super.onDestroy()
    }

}