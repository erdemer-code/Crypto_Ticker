package com.erdemer.cryptoticker.util

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.erdemer.cryptoticker.ui.coinDetail.manager.CoinDetailRequestServiceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CoinDetailService : Service() {

    @Inject
    lateinit var coinDetailRequestServiceManager: CoinDetailRequestServiceManager

    companion object {
        const val COIN_ID = "coin_id"
        const val TIME_INTERVAL = "time_interval"
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val coinId = intent?.getStringExtra(COIN_ID)
        val timeInMillis = intent?.getLongExtra(TIME_INTERVAL, -1)
        Log.d("Service", "onStartCommand: $coinId---$timeInMillis")
        coinId?.let {
            if (timeInMillis != -1L){
                Handler(Looper.getMainLooper()).postDelayed({
                    coinDetailRequestServiceManager.invoke(
                        coinId
                    )
                }, timeInMillis!!)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}