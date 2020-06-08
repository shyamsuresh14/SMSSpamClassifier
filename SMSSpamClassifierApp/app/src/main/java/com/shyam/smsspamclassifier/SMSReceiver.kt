package com.shyam.smsspamclassifier

import android.R.id
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import org.greenrobot.eventbus.EventBus

/*Broadcast Receiver which acquires the incoming SMS messages*/
class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val bundle = intent.extras
            if (bundle != null) {
                // get sms objects
                val pdus = bundle["pdus"] as Array<*>?
                if (pdus!!.isEmpty()) {
                    return
                }
                // large message might be broken into many
                val messages: Array<SmsMessage?> = arrayOfNulls<SmsMessage>(pdus.size)
                val sb = StringBuilder()
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    sb.append(messages[i]!!.messageBody)
                }
                val sender: String = messages[0]!!.originatingAddress.toString()
                val message = sb.toString()
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                val msg = Message(message, System.currentTimeMillis().toString(), sender)
                Log.d("TFLITE", "Received message, $message from $sender")
                /*Publisher*/
                EventBus.getDefault().post(OnReceiverMessage(msg))
                abortBroadcast()  //block other broadcast receivers
            }
        }
    }
}
