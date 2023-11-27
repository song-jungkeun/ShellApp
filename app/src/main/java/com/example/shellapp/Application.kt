package com.example.shellapp

import android.app.Application
import android.content.Intent
import android.widget.Toast
import android_serialport_api.SerialPort

class Application: Application() {
    lateinit var list : ArrayList<Appliance>
    lateinit var serialList: ArrayList<SerialPort>

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, MyService::class.java)
        startService(intent)
        Toast.makeText(this, "Service start", Toast.LENGTH_SHORT).show()
        list = ArrayList()
        serialList = ArrayList()
    }
}