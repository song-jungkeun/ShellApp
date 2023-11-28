package com.example.shellapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android_serialport_api.SerialPort
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shellapp.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private val SERIAL_BAUDRATE = 9600
    private lateinit var binding: ActivityMainBinding
    lateinit var getResultText: ActivityResultLauncher<Intent>
    lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val app = application as Application

        customAdapter = CustomAdapter(app.list)


        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val applianceType = intent?.getStringExtra("type")
                    val serialPortNum = intent?.getStringExtra("portNum")
                    val applianceName = intent?.getStringExtra("applianceName")
                    app.list.add(Appliance(applianceType!!, serialPortNum!!, applianceName!!))
                    setSerialPort(serialPortNum)?.let {
                        app.serialList.add(it)
                        app.outputStreamList.add(it.outputStream)
                    }
                    customAdapter.notifyDataSetChanged()
                }
            }

        binding.mRecyclerView.adapter = customAdapter
        binding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.plusBtn.setOnClickListener {
            val intent = Intent(this, AddApplianceActivity::class.java)
            startForResult.launch(intent)
        }
    }

    private fun setSerialPort(name: String?) : SerialPort? {
        try {
            val serialPort = SerialPort(
                File("$name"),
                SERIAL_BAUDRATE,
                0
            )
            Toast.makeText(this, "output stream", Toast.LENGTH_SHORT).show()
            return serialPort
        } catch (e: IOException) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        return null
    }

}
