package com.shyam.smsreader

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private val permissionRequestCode = 100;
    private val permissions : Array<String> = arrayOf(android.Manifest.permission.READ_SMS, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!permissionGranted())
            requestPermission();
        else
            readMessages()
    }
    private fun readMessages(){
        val cursor:Cursor? = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        if(cursor?.moveToFirst() == true){
            val msgs = cursor.count
            progressBar.max = 100
            Toast.makeText(this, msgs.toString(), Toast.LENGTH_SHORT).show()
            val fld = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())
            val file = File(fld, "sms_data.txt")
            try{
                file.createNewFile()
                val fout = FileOutputStream(file, true)
                val writer = OutputStreamWriter(fout)
                Toast.makeText(this, "Writing!", Toast.LENGTH_SHORT).show()
                var msg = ""
                for(i in 0 until cursor.columnCount)
                    msg += cursor.getColumnName(i) + (if (i == cursor.columnCount - 1) "\n" else ",")
                writer.append(msg)
                var cnt = 0.0f
                do {
                    msg = ""
                    for(i in 0 until cursor.columnCount)
                        msg += cursor.getString(i) + (if (i == cursor.columnCount - 1) "\n" else ",")
                    writer.append(msg)
                    progressBar.progress = ((cnt/msgs) * 100).toInt(); cnt++
                }while(cursor.moveToNext())
                writer.close()
                fout.close()
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()
                //findViewById<TextView>(R.id.message).text = msg
            }catch (e: Exception) {
                Toast.makeText(this, "Write failed", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }else{
            Log.d("SMS", "Inbox empty")
        }
        cursor?.close()
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
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) readMessages()
                else requestPermission()
                return;
            }
        }
    }
}
