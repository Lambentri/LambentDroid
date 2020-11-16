package io.gmp.does.lambent.droid

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView



class LinkListAdapter internal constructor() : RecyclerView.Adapter<LinkMainViewHolder>() {
    var items = emptyList<Link>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkMainViewHolder {
        return LinkMainViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: LinkMainViewHolder, position: Int) {
        val device: Link = items[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun setLinks(places: List<Link>) {
        this.items = places
        notifyDataSetChanged()
    }
}

class LinkMainViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.links_item, parent, false)) {

    lateinit var item: Link
    var label_name: TextView
    var label_from: TextView
    var label_to: TextView
    var button_rm: ImageButton
    var toggle: Switch

    init {
        label_name = itemView.findViewById(R.id.link_name)
        label_from = itemView.findViewById(R.id.link_from)
        label_to = itemView.findViewById(R.id.link_to)
        button_rm = itemView.findViewById(R.id.link_button_rename)
        toggle = itemView.findViewById(R.id.link_switch)

        button_rm.setOnClickListener {
            Toast.makeText(parent.context, "RM Clicked", Toast.LENGTH_SHORT).show()
        }
        toggle.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            Toast.makeText(parent.context, "Toggle Clicked {$isChecked}", Toast.LENGTH_SHORT).show()
        }

    }

    fun bind(device: Link) {
        item = device
        label_name.text = device.list_name
        label_from.text = device.full_spec.source.listname
        label_to.text = device.full_spec.target.listname
        toggle.isChecked = device.active

    }
}
