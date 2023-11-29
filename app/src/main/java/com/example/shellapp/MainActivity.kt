package com.example.shellapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android_serialport_api.SerialPort
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shellapp.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val SERIAL_BAUDRATE = 9600
    private lateinit var binding: ActivityMainBinding
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

                    setSerialPort(serialPortNum)?.also {
                        app.list.add(Appliance(applianceType!!, serialPortNum!!, applianceName!!))
                        app.serialList.add(it)
                        app.outputStreamList.add(it.outputStream)
                        app.inputStreamList.add(it.inputStream)
                        customAdapter.notifyDataSetChanged()
                    }

                    // 테스트용
//                    app.list.add(Appliance(applianceType!!, serialPortNum!!, applianceName!!))
//                    customAdapter.notifyDataSetChanged()

                }
            }

        binding.mRecyclerView.adapter = customAdapter
        binding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.plusBtn.setOnClickListener {
            val intent = Intent(this, AddApplianceActivity::class.java)
            startForResult.launch(intent)
        }

        customAdapter.setOnItemLongClickListener { pos ->
            val dialog = AlertDialog.Builder(this)

            dialog.apply {
                setMessage("삭제할까요?")
                setPositiveButton("Yes") { _, _ ->
                    app.list.removeAt(pos)
                    app.serialList.removeAt(pos)
                    app.outputStreamList.removeAt(pos)
                    app.inputStreamList.removeAt(pos)
                    customAdapter.notifyDataSetChanged()
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }


    }

    private fun setSerialPort(name: String?): SerialPort? {
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
