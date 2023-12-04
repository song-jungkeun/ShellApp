package com.example.shellapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android_serialport_api.SerialPort
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shellapp.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.io.InputStream

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
                        val hexValues =
                            byteArrayOf(0xAD.toByte(), 0x53.toByte(), 0x00.toByte(), 0x7E.toByte())
                        it.outputStream.write(hexValues)
//                        readData(it.inputStream)
                    }

                    // 테스트용
//                    app.list.add(Appliance(applianceType!!, serialPortNum!!, applianceName!!))
//                    customAdapter.notifyDataSetChanged()

                }
            }

//        binding.button2.setOnClickListener {
//            Toast.makeText(this, "input stream", Toast.LENGTH_SHORT).show()
//            readData(app.inputStreamList[0])
////            answer += "read start\n"
//            binding.textView.text = answer
//        }

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

//    private lateinit var serialThread: Thread
//    private var answer = ""
//
//    private fun readData(inputStream: InputStream): Boolean {
//        if (inputStream == null) {
//            answer += "input stream is null\n"
//            binding.textView.text = answer
//            return false
//        }
//        serialThread = Thread {
//            answer += "Thread start\n"
//            binding.textView.text = answer
//            runOnUiThread {
//                Toast.makeText(this, "thread start", Toast.LENGTH_SHORT)
//            }
//            while (true) {
//                try {
//
//                    var buffer = ByteArray(12800)
//                    var size = 0
//                    size = inputStream.read(buffer)
//                    answer += "size = $size\n"
//                    binding.textView.text = answer
////                    val str = onReceiveData(buffer, size ?: 0)
//                } catch (e: IOException) {
//                    answer += "Thread catch error\n"
//                    binding.textView.text = answer
//                }
//            }
//        }
//        serialThread.start()
//
//        return true
//    }
//
//    private fun onReceiveData(buffer: ByteArray, size: Int): String? {
//        if (size < 1) return null
//
//        runOnUiThread {
//            Toast.makeText(this, "2", Toast.LENGTH_SHORT)
//        }
//        answer += "\nOnReceiveData -> start"
//        binding.textView.text = answer
//        var strBuilder = StringBuilder()
//        for (i in 0 until size) {
//            strBuilder.append(String.format("%02x", buffer[i]))
//        }
//        answer += "\nOnReceiveData -> strBuilder=$strBuilder"
//        binding.textView.text = answer
//        runOnUiThread {
//            Toast.makeText(this, "rx=$strBuilder", Toast.LENGTH_SHORT).show()
//        }
////        Toast.makeText(this, "rx=$strBuilder", Toast.LENGTH_SHORT).show()
//        Log.d("serialExam", "rx : $strBuilder")
//        return strBuilder.toString()
//    }

}
