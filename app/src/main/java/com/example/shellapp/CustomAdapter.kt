package com.example.shellapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.shellapp.databinding.SerialContactsBinding

class CustomAdapter(private val applianceList: ArrayList<Appliance>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private lateinit var onItemLongClickListener: OnItemLongClickListener

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    inner class ViewHolder(val binding: SerialContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(type: String, serialPort: String, name: String) {
            binding.applianceType.text = type
            binding.serialPort.text = serialPort
            binding.applianceName.text = name
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        return ViewHolder(
            SerialContactsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: CustomAdapter.ViewHolder, position: Int) {
        viewHolder.bind(
            applianceList[position].applianceType,
            applianceList[position].serialPortNum,
            applianceList[position].applianceName
        )
        with(viewHolder) {
            binding.container.setOnLongClickListener {
                onItemLongClickListener.onItemLongClick(position)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return applianceList.size
    }

    fun setOnItemLongClickListener(onItemLongClick: (Int) -> Unit) {
        onItemLongClickListener = object : OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                onItemLongClick(position)
            }
        }
    }

}