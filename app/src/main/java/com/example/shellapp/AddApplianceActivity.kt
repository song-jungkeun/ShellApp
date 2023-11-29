package com.example.shellapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android_serialport_api.SerialPortFinder
import androidx.appcompat.app.AppCompatActivity
import com.example.shellapp.databinding.ActivityAddApplianceBinding


class AddApplianceActivity : AppCompatActivity() {


    private lateinit var applianceType: String
    private lateinit var serialPortNum: String
    private var serialPortFinder: SerialPortFinder = SerialPortFinder()
    private lateinit var binding: ActivityAddApplianceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddApplianceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var data = listOf("LED", "카메라", "에어컨")
        var applianceAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        binding.appliance.adapter = applianceAdapter
        binding.appliance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applianceType = data.get(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
//         RS485에 연결되어 있는 Port 의 정보를 entries 배열에 담는다
        val entries: Array<String> = serialPortFinder.allDevicesPath
//         이름 순서대로 정렬
        entries.sort()

        // 테스트용
//        val entries = arrayOf("dev/ttyAMA0", "dev/ttyAMA1", "dev/ttyAMA2")

        var serialPortAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
        binding.serialPort.adapter = serialPortAdapter
        binding.serialPort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                serialPortNum = entries.get(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val applianceName = binding.editApllianceName.text.toString()

            val app = application as Application
            val duplicatedName = app.list.find { it.applianceName == applianceName }
            val duplicatedPort = app.list.find { it.serialPortNum == serialPortNum }
            if (duplicatedName != null) {
                Toast.makeText(this, "Name is duplicated", Toast.LENGTH_SHORT).show()
            } else if (duplicatedPort != null) {
                Toast.makeText(this, "SerialPort is duplicated", Toast.LENGTH_SHORT).show()
            } else {
                intent.putExtra("type", applianceType)
                intent.putExtra("portNum", serialPortNum)
                intent.putExtra("applianceName", applianceName)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        binding.buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

    }


}