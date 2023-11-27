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
    private val SERIAL_PORT_NAME = "ttyAMA0"

    private var serialPort: SerialPort? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    lateinit var serialThread: Thread

    val list = ArrayList<Appliance>()

    private lateinit var binding: ActivityMainBinding
    lateinit var getResultText: ActivityResultLauncher<Intent>
    var customAdapter: CustomAdapter = CustomAdapter(list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val startforResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val applianceType = intent?.getStringExtra("type")
                    val serialPortNum = intent?.getStringExtra("portNum")
                    val applianceName = intent?.getStringExtra("applianceName")
                    list.add(Appliance(applianceType!!, serialPortNum!!, applianceName!!))
                    customAdapter.notifyDataSetChanged()
                }
            }

        binding.mRecyclerView.adapter = customAdapter
        binding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.plusBtn.setOnClickListener {
            val intent = Intent(this, AddApplianceActivity::class.java)
            startforResult.launch(intent)
        }


    }

    fun SetSerialPort(name: String?) {
        try {
            serialPort = SerialPort(
                File(name),
                SERIAL_BAUDRATE,
                0
            )
            outputStream = serialPort!!.outputStream
            Toast.makeText(this, "output stream", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "eeror", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun StartRxThread() {
        if (inputStream == null) {
            Log.e("SerialExam", "Can't open inputstream")
            return
        }

        serialThread = Thread {
            while (true) {
                try {
                    var buffer = ByteArray(64)
                    val size = inputStream?.read(buffer)
                    OnReceiveData(buffer, size ?: 0)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        serialThread.start()
    }

    private fun OnReceiveData(buffer: ByteArray, size: Int) {
        if (size < 1) return

        var strBuilder = StringBuilder()
        for (i in 0 until size) {
            strBuilder.append(String.format("%02x", buffer[i]))
        }
        Log.d("serialExam", "rx : " + strBuilder.toString())
    }

    fun sendData(inputString: String) {
        val byteArray: ByteArray =
            convertHexStringToByteArray(inputString)

        // byteArray를 사용할 수 있습니다.
        for (b in byteArray) {
            Log.d("test", "0x" + String.format("%02X", b))
        }
        try {
            if (outputStream != null) {
                outputStream!!.write(byteArray)
            }
        } catch (e: IOException) {
        }
    }

    private fun convertHexStringToByteArray(hexString: String): ByteArray {
        val hexValues = hexString.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val byteArray = ByteArray(hexValues.size)
        Log.d("test", "size=" + hexValues.size)
        for (i in hexValues.indices) {
            // "0x"를 제거하고 문자열을 16진수로 파싱하여 바이트로 변환
            val hexValue = hexValues[i].trim { it <= ' ' }.replace("0x", "")
            val byteValue = hexValue.toInt(16).toByte()
            byteArray[i] = byteValue
        }
        return byteArray
    }

    override fun onDestroy() {
//        serialThread.interrupt();
        serialPort!!.close()
        super.onDestroy()
    }
}
