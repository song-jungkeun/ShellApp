package com.example.shellapp

import android.app.Application
import android_serialport_api.SerialPort

class Application: Application() {
    lateinit var list : ArrayList<Appliance>
    lateinit var serialList: ArrayList<SerialPort>

    override fun onCreate() {
        super.onCreate()

        list = ArrayList()
        serialList = ArrayList()
    }
}