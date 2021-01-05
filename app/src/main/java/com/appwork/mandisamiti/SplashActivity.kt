package com.appwork.mandisamiti

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.appwork.data.DataPref
import com.appwork.ui.auth.RegisterScreenActivity
import com.appwork.ui.client.ClientListActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SplashActivity : AppCompatActivity(), KodeinAware {
    companion object {
        const val timeInterval = 1000L
        const val totalTimeInterval = 2000L
    }

    private val prefs: DataPref by instance()

    override val kodein by kodein()

    private var countDownTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        countDownTimer = object : CountDownTimer(totalTimeInterval, timeInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                prefs.userContact.asLiveData().observe(this@SplashActivity, Observer {
                    if (it.isNullOrEmpty()) {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, ClientListActivity::class.java))
                    }
                    finish()
                })
            }
        }
        countDownTimer!!.start()
    }

}