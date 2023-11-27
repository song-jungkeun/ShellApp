package com.example.shellapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android_serialport_api.SerialPort
import androidx.core.app.NotificationCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MyService : Service() {

    val CHANNEL_ID = "ForegroundServiceChannel"

    private lateinit var serialThread: Thread
    private lateinit var app: Application

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("data")

        setBroadcastReceiver()
        createNotificationChannel()

        app = application as Application

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText(input)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
//        TODO("Return the communication channel to the service.")
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            assert(manager != null)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun sendDataToSmartHome(cmd: String, json: JSONObject) {
        val intent = Intent("com.example.ACTION_SEND_DATA")
        intent.putExtra(cmd, json.toString())
        sendBroadcast(intent)
    }

    private fun setBroadcastReceiver() {
        val filter = IntentFilter("com.example.ACTION_SEND_DATA_SHELL")
        filter.addAction("com.example.ACTION_SEND_DATA_SHELL")
        registerReceiver(mReceiver, filter)
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.ACTION_SEND_DATA_SHELL" -> {
                    Log.d("test mReceiver", "${intent.getStringExtra("data")}")
                    val json = JSONObject(intent.getStringExtra("data"))
                    val cmd = json.getString("cmd")

                    if (cmd == "getApplianceList") {
                        for (i in app.list) {
                            Log.d("test list", "$i")

                        }

                        val jsonArray = JSONArray()
                        for (i in app.list) {
                            val jsonObject = JSONObject()
                                .put("type", i.applianceType)
                                .put("name", i.applianceName)
                            jsonArray.put(jsonObject)
                        }

                        Log.d("test jsonArray", "jsonArray = $jsonArray")

                        val jsonData = JSONObject()
                            .put("applianceList", jsonArray)
                        Log.d("test jsonData", "jsonData = $jsonData")
                        sendDataToSmartHome("retApplianceList", jsonData)
                    } else if (cmd == "control") {
                        val type = json.getString("type")
                        val name = json.getString("name")
                        val msg = json.getString("msg")

                        // serial port led on func
                        Log.d("test appliance", "$type")
                        Log.d("test appliance", "$name")
                        if (type == "LED") {
                            Log.d("test LED", msg)
                            if (msg == "lightOn") {
                                // LED turn on light
                                val index =
                                    app.list.indexOf(app.list.find { it.applianceName == name })

                                turnOnLED(index)
                            } else if (msg == "lightOff") {
                                val index =
                                    app.list.indexOf(app.list.find { it.applianceName == name })

                                turnOffLED(index)
                            }
                        } else if (type == "Camera") {
                            Log.d("test Camera", msg)
                            // camera control
                        }


                    }
                }
            }
        }
    }

    private fun turnOnLED(index: Int) : Boolean{
        val outputStream = app.serialList[index].outputStream
        val packet = "AD 53 01 7F"
        val ret = sendData(packet, outputStream)
        outputStream.close()
        return ret
    }

    private fun turnOffLED(index: Int) :Boolean{
        val outputStream = app.serialList[index].outputStream
        val packet = "AD 53 00 7E"
        val ret = sendData(packet, outputStream)
        outputStream.close()
        return ret
    }


    private fun sendData(inputString: String, outputStream: OutputStream) : Boolean {
        val byteArray: ByteArray = convertHexStringToByteArray(inputString)

        try {
            outputStream.write(byteArray)
            return true
        } catch (e: IOException) {
            Log.d("test sendData", "error")
        }
        return false
    }

//    private fun StartRxThread() {
//        if (inputStream == null) {
//            Log.e("SerialExam", "Can't open inputstream")
//            return
//        }
//
//        serialThread = Thread {
//            while (true) {
//                try {
//                    var buffer = ByteArray(64)
//                    val size = inputStream?.read(buffer)
//                    OnReceiveData(buffer, size ?: 0)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//        serialThread.start()
//    }

//    private fun OnReceiveData(buffer: ByteArray, size: Int) {
//        if (size < 1) return
//
//        var strBuilder = StringBuilder()
//        for (i in 0 until size) {
//            strBuilder.append(String.format("%02x", buffer[i]))
//        }
//        Log.d("serialExam", "rx : " + strBuilder.toString())
//    }


    private fun convertHexStringToByteArray(hexString: String): ByteArray {
        val hexValues = hexString.split(" ")
        val byteArray = ByteArray(hexValues.size)
        Log.d("test", "size=" + hexValues.size)
        for (i in hexValues.indices) {
            // "0x"를 제거하고 문자열을 16진수로 파싱하여 바이트로 변환
            val byteValue = hexValues[i].toInt(16).toByte()
            byteArray[i] = byteValue
        }
        return byteArray
    }
}