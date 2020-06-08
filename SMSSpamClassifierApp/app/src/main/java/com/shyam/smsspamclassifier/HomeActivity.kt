package com.shyam.smsspamclassifier

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException

/*Launcher Activity: Demonstration screen. Shows the message, sender and classification output*/
class HomeActivity : AppCompatActivity() {
    private val permissionRequestCode = 100;
    private val permissions : Array<String> = arrayOf(android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS)
    private lateinit var sender: TextView
    private lateinit var message: TextView
    private lateinit var output: TextView
    private lateinit var classifier: Classifier
    private lateinit var preprocessor: Preprocessor
    private lateinit var dbAccess: DBAccess
    private lateinit var receivedMessage: Message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if(intent.getStringExtra("message") != null){
            log(intent.getStringExtra("message").toString())
        }

        sender = findViewById<TextView>(R.id.sender_view)
        message = findViewById<TextView>(R.id.message_view)
        output = findViewById<TextView>(R.id.output_view)

        if(!permissionGranted())
            requestPermission()
        else{
            init()
            initModel()

            findViewById<Button>(R.id.show_all).setOnClickListener{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun init(){
        if (!isServiceRunning(BackgroundService::class.java)) startService(
            Intent(
                this,
                BackgroundService::class.java
            )
        )
    }

    private fun initModel(){
        try {
            classifier = Classifier(this)
            preprocessor = Preprocessor(this)
            preprocessor.setCallback(object: Preprocessor.outputCallback{
                override fun onPreprocessed(preprocessedMsg: FloatArray){
                    predict(preprocessedMsg)
                }
            })
            dbAccess = DBAccess(this)
            log("Successfully loaded!")
        }catch (e: IOException){
            log("Failed")
            e.printStackTrace()
        }
    }

    private fun isServiceRunning(className: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (serviceInfo in manager.getRunningServices(Int.MAX_VALUE)) if (serviceInfo.service.className == className.name) return true
        return false
    }

    private fun permissionGranted() : Boolean {
        for(permission in permissions)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) return false
        return true
    }

    private fun requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])
            || ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1])){

        }else{
            ActivityCompat.requestPermissions(this, permissions, permissionRequestCode);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            permissionRequestCode -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) init()
                else requestPermission()
                return;
            }
        }
    }
    /*Subscriber*/
    @Subscribe
    fun onMessageReceived(onReceiverMessage: OnReceiverMessage){
        receivedMessage = onReceiverMessage.message
        //val newText = "${sender.text} ${msg.sender}"
        sender.text = "Sender: ${receivedMessage.sender}"
        message.text = "Message: ${receivedMessage.body}"
        preprocessor.preprocess(receivedMessage.body)
    }
    /*perform classification and update UI*/
    fun predict(preprocessedMsg: FloatArray){
        runOnUiThread {
            receivedMessage.label = classifier.classify(preprocessedMsg)
            output.text = "Model Output: ${receivedMessage.label}"
            //dbAccess.store(receivedMessage)
        }
    }

    private fun log(msg: String){
        Log.d("TFLITE OP", msg)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
        stopService(Intent(this, BackgroundService::class.java))
    }
}
