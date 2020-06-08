package com.shyam.smsspamclassifier

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/*Runs in the background and manages the SMS Broadcast Receiver*/
class BackgroundService : Service() {
    private var mReciever: SMSReceiver? = null

    fun ReceiverService() {}

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val c: Calendar = Calendar.getInstance()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val time: String = df.format(c.time)
        log("Service started at $time")
        mReciever = SMSReceiver()
        registerReceiver(mReciever, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        log( "Receiver started at $time")
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun log(msg: String){
        Log.d("TFLITE OP", msg);
    }

    override fun onDestroy() {
        super.onDestroy()
        val c: Calendar = Calendar.getInstance()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val time: String = df.format(c.getTime())
        log("Service stopped at $time")
        unregisterReceiver(mReciever)
    }
}
