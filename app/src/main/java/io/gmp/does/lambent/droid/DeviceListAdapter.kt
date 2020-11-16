package io.gmp.does.lambent.droid

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView



class DeviceListAdapter internal constructor() : RecyclerView.Adapter<DeviceMainViewHolder>() {
    var items = emptyList<Device>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceMainViewHolder {
        return DeviceMainViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: DeviceMainViewHolder, position: Int) {
        val device: Device = items[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun setDevices(places: List<Device>) {
        this.items = places
        notifyDataSetChanged()
    }
}

class DeviceMainViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.devices_item, parent, false)) {

    lateinit var item: Device
    var label_name: TextView
    var button_flash: ImageButton
    var button_rename: ImageButton

    init {
        label_name = itemView.findViewById(R.id.device_name)
        button_flash = itemView.findViewById(R.id.device_button_flash)
        button_rename = itemView.findViewById(R.id.device_button_rename)

        button_flash.setOnClickListener {
            Toast.makeText(parent.context, "Flash Clicked", Toast.LENGTH_SHORT).show()
        }

        button_rename.setOnClickListener {
            Toast.makeText(parent.context, "Rename Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    fun bind(device: Device) {
        item = device
        label_name.text = item.iname
    }
}
