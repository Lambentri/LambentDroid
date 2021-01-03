package io.gmp.does.lambent.droid

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import io.gmp.does.lambent.droid.ui.main.MainViewModel

const val DELIM = "·"

class LinkListAdapter internal constructor(viewModel: MainViewModel) :
    RecyclerView.Adapter<LinkMainViewHolder>() {
    val viewModel = viewModel
    var items = emptyList<Link>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkMainViewHolder {
        return LinkMainViewHolder(LayoutInflater.from(parent.context), parent, viewModel)
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

class LinkMainViewHolder internal constructor(
    inflater: LayoutInflater,
    parent: ViewGroup,
    viewModel: MainViewModel
) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.links_item, parent, false)) {

    lateinit var item: Link
    var label_name: TextView
    var label_from: TextView
    var label_to: TextView

    //    var button_rm: ImageButton
    var toggle: Switch
    var toggling: Boolean = false

    init {
        label_name = itemView.findViewById(R.id.link_name)
        label_from = itemView.findViewById(R.id.link_from)
        label_to = itemView.findViewById(R.id.link_to)
//        button_rm = itemView.findViewById(R.id.link_button_delete)
        toggle = itemView.findViewById(R.id.link_switch)

//        button_rm.setOnClickListener {
//            Toast.makeText(parent.context, "RM Clicked", Toast.LENGTH_SHORT).show()
//        }
        toggle.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            if (buttonView.isPressed()) {
                Toast.makeText(parent.context, "Toggle Clicked {$isChecked}", Toast.LENGTH_SHORT)
                    .show()
//                viewModel.link_toggle(item)
//                toggle.toggle()
            }

        }

    }

    fun bind(link: Link) {
        item = link
        if (DELIM in link.name) {
            label_name.text = link.name.split(DELIM)[0] + DELIM
        } else {
            label_name.text = link.name
        }
        label_from.text = link.full_spec.source.list_name
        label_to.text = link.full_spec.target.list_name
        toggle.isChecked = link.active

    }
}
