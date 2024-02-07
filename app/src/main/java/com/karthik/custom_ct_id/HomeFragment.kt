package com.karthik.custom_ct_id

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.clevertap.android.sdk.CleverTapAPI
import com.google.android.material.textfield.TextInputEditText
import com.karthik.custom_ct_id.databinding.FragmentHomeBinding
import java.util.Timer
import kotlin.concurrent.schedule

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var androidID: String
    lateinit var cleverTapId: String

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        androidID = Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID)
        println("CT ID before onUserLogin $androidID")
        val time = System.currentTimeMillis()
        println("Time: $time")

        cleverTapId = androidID + time

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)

        CleverTapAPI.getDefaultInstance(context)!!.enablePersonalization()

        CleverTapAPI.getDefaultInstance(context)?.enableDeviceNetworkInfoReporting(true)

        CleverTapAPI.isAppForeground()

        Timer().schedule(5000) {
            CleverTapAPI.getDefaultInstance(context)?.getCleverTapID {
                println("The current CT ID Timer().schedule before onUserLogin is $it")
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        androidID = Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID)
        println("CT ID before onUserLogin $androidID")
        val time = System.currentTimeMillis()
        println("Time: $time")

        cleverTapId = androidID + time

//        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(context, cleverTapId)

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)

        CleverTapAPI.getDefaultInstance(context)!!.enablePersonalization()

        CleverTapAPI.getDefaultInstance(context)!!.enableDeviceNetworkInfoReporting(true)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.on_user_login).setOnClickListener {
            onUserLogin(view)
        }

        view.findViewById<Button>(R.id.push_profile).setOnClickListener {
            CleverTapAPI.getDefaultInstance(context)?.getCleverTapID {
                println("The current CT ID is $it")
            }
        }

        view.findViewById<Button>(R.id.upload_event_btn).setOnClickListener {
            uploadEventToCT(view)
        }

    }

    private fun onUserLogin(view: View) {
        val profile = HashMap<String, Any>()
        profile["Name"] = view.findViewById<TextInputEditText>(R.id.user_name).text.toString()
        profile["Identity"] = view.findViewById<TextInputEditText>(R.id.user_identity).text.toString()
        profile["Email"] = view.findViewById<TextInputEditText>(R.id.email_id).text.toString()
        profile["Phone"] = view.findViewById<TextInputEditText>(R.id.mobile_no).text.toString()
        profile["MSG-email"] = true
        profile["MSG-push"] = true
        profile["MSG-sms"] = true
        profile["MSG-whatsapp"] = true

        val newCTID = androidID + view.findViewById<TextInputEditText>(R.id.user_identity).text.toString()
        println("CT ID After onUserLogin $newCTID")

        CleverTapAPI.getDefaultInstance(context)?.onUserLogin(profile, newCTID).apply {
            CleverTapAPI.getDefaultInstance(context)?.pushEvent("Login Successful")
        }

        Toast.makeText(context, "Logged in!", Toast.LENGTH_SHORT).show()

        CleverTapAPI.setAppForeground(true)

        Timer().schedule(3000) {
            CleverTapAPI.getDefaultInstance(context)?.getCleverTapID {
                println("The current CT ID Timer().schedule after onUserLogin is $it")
            }
        }

    }

    private fun uploadEventToCT(view: View) {
        val value = view.findViewById<TextInputEditText>(R.id.up_event).text.toString()
        println("event name: $value")
        CleverTapAPI.getDefaultInstance(context)?.pushEvent(value)
        Toast.makeText(context, "Event Raised!", Toast.LENGTH_SHORT).show()
    }

}