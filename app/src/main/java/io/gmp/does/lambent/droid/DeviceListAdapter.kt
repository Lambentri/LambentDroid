package io.gmp.does.lambent.droid

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils.getActivity
import io.gmp.does.lambent.droid.ui.main.DeviceRenameDialog
import io.gmp.does.lambent.droid.ui.main.MainViewModel


class DeviceListAdapter internal constructor(viewModel: MainViewModel) :
    RecyclerView.Adapter<DeviceMainViewHolder>() {
    val viewModel = viewModel
    var items = emptyList<Device>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceMainViewHolder {
        return DeviceMainViewHolder(
            LayoutInflater.from(parent.context),
            parent,
            viewModel
        )
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

class DeviceMainViewHolder internal constructor(
    inflater: LayoutInflater,
    parent: ViewGroup,
    viewModel: MainViewModel
) :
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
            viewModel.device_poke(item.iname)
        }

        button_rename.setOnClickListener {
            val activity: MainActivity = parent.context as MainActivity
            activity.showRenameDialog(item, viewModel)
        }
    }

    fun bind(device: Device) {
        item = device
        label_name.text = item.name
    }
}
