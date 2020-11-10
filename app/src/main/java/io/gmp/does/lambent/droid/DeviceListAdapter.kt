package io.gmp.does.lambent.droid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PlaceMainViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.devices_item, parent, false)) {

    lateinit var item: Device

    init {
    }

    fun bind(device: Device) {
        item = device
    }
}


class DeviceListAdapter internal constructor() : RecyclerView.Adapter<PlaceMainViewHolder>() {
    var items = emptyList<Device>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceMainViewHolder {
        return PlaceMainViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: PlaceMainViewHolder, position: Int) {
        val device: Device = items[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun setPlaces(places: List<Device>) {
        this.items = places
        notifyDataSetChanged()
    }
}