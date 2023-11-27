package com.example.shellapp

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shellapp.databinding.ActivityMainBinding
import com.example.shellapp.databinding.SerialContactsBinding

class CustomAdapter(val ApplianceList : ArrayList<Appliance>) : RecyclerView.Adapter<CustomAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.Holder {
        val binding = SerialContactsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: CustomAdapter.Holder, position: Int) {
        holder.type.text = ApplianceList[position].applianceType
        holder.serialPort.text = ApplianceList[position].serialPortNum
        holder.name.text = ApplianceList[position].applianceName
    }

    override fun getItemCount(): Int {
        return ApplianceList.size
    }

    inner class Holder(val binding: SerialContactsBinding): RecyclerView.ViewHolder(binding.root){
        val type = binding.applianceType
        val serialPort = binding.serialPort
        val name = binding.applianceName
    }
}