package io.gmp.does.lambent.droid

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView



class MachineListAdapter internal constructor() : RecyclerView.Adapter<MachineMainViewHolder>() {
    var items = emptyList<Machine>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineMainViewHolder {
        return MachineMainViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: MachineMainViewHolder, position: Int) {
        val device: Machine = items[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun setMachines(places: List<Machine>) {
        this.items = places
        notifyDataSetChanged()
    }
}

class MachineMainViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.machines_item, parent, false)) {

    lateinit var item: Machine
    var label_name: TextView
    var label_type: TextView
    var label_speed: TextView
    var button_plus: ImageButton
    var button_minus: ImageButton
    var button_playpause: ImageButton

    init {
        label_name = itemView.findViewById(R.id.machine_name)
        label_type = itemView.findViewById(R.id.machine_type)
        label_speed = itemView.findViewById(R.id.machine_speed)
        button_plus = itemView.findViewById(R.id.machine_button_plus)
        button_minus = itemView.findViewById(R.id.machine_button_minus)
        button_playpause = itemView.findViewById(R.id.machine_button_playpause)

        button_plus.setOnClickListener {
            Toast.makeText(parent.context, "Faster Clicked", Toast.LENGTH_SHORT).show()
        }
        button_minus.setOnClickListener {
            Toast.makeText(parent.context, "Slower Clicked", Toast.LENGTH_SHORT).show()
        }
        button_playpause.setOnClickListener {
            Toast.makeText(parent.context, "Play/Pause Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    fun bind(device: Machine) {
        item = device
        label_name.text = item.iname
        label_type.text = item.name
        label_speed.text = item.speed.toString()
    }
}
