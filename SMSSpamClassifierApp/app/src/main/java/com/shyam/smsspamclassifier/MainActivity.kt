package com.shyam.smsspamclassifier

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import java.io.IOException
import java.lang.Float.parseFloat

class MainActivity : AppCompatActivity() {
    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        //testRun()
    }

    private fun initUI(){
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.offscreenPageLimit = 2
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val fragment1 = MessageListFragment.newInstance("Ham")
        val fragment2 = MessageListFragment.newInstance("Spam")

        viewPagerAdapter.addFragment(fragment1, "Ham")
        viewPagerAdapter.addFragment(fragment2, "Spam")
        viewPager.adapter = viewPagerAdapter
    }

    private fun log(msg: String){
        Log.d("TFLITE OP", msg);
    }

    /*Functions below were used for testing the classifer and dbAccess*/
    private fun testRun(){
        val dbAccess = DBAccess(this)
        //dbAccess.delete(25)
        //dbAccess.update(12, "Spam")
        val msg = dbAccess.retrieve()
        log(msg[0].id.toString() + " " + msg[0].body + " " + msg[0].label)
    }

    private fun initModel(){
        try {
            classifier = Classifier(this)
            log("Successfully loaded!")
        }catch (e: IOException){
            log("Failed")
            e.printStackTrace()
        }
    }

    private fun getSampleInput() : Array<FloatArray>{
        val fin = this.assets.open("test_1.txt")
        val data = fin.readBytes().toString(Charsets.UTF_8)
        var input = arrayOf<FloatArray>()
        for(d in data.split("\n")){
            if(!d.isBlank()) {
                val row = d.split(",").map { parseFloat(it.trim()) }.toFloatArray()
                input += row
            }
        }
        log(input.size.toString() + " " + input[0].size.toString())
        return input
    }
}
