package com.karthik.custom_ct_id

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.clevertap.android.sdk.CleverTapAPI
import com.karthik.custom_ct_id.databinding.ActivityMainBinding
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var cleverTapDefaultInstance: CleverTapAPI? = null
    lateinit var androidID: String
    lateinit var cleverTapId: String

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        androidID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        println("CT ID before onUserLogin $androidID")
        val time = System.currentTimeMillis()
        println("Time: $time")

        cleverTapId = androidID + time

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext, cleverTapId)

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)

        CleverTapAPI.getDefaultInstance(applicationContext)!!.enablePersonalization()

        cleverTapDefaultInstance?.enableDeviceNetworkInfoReporting(true)

        Timer().schedule(5000) {
            CleverTapAPI.getDefaultInstance(applicationContext)?.getCleverTapID {
                println("The current CT ID Timer().schedule before onUserLogin from activity is $it")
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<HomeFragment>(R.id.home_fragement_holder)
            }
        }

    }
}