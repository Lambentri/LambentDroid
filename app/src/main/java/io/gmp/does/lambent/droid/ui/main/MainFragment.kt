package io.gmp.does.lambent.droid.ui.main

import android.content.DialogInterface
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.gmp.does.lambent.droid.*
import kotlinx.android.synthetic.main.fragment_tab_devices.*
import kotlinx.android.synthetic.main.fragment_tab_links.*
import kotlinx.android.synthetic.main.fragment_tab_machines.*
import kotlinx.android.synthetic.main.link_fab_submenu.*


val Labels = listOf<String>(
    "Devices",
    "Machs",
    "Links",
    "Zones"
//    "Sets"
)

private const val ARG_ID = "id"
private const val ARG_OBJECT = "object"

class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    lateinit var viewModel: MainViewModel

    override fun getItemCount(): Int = Labels.size
//    lateinit var fragment: Fragment

    override fun createFragment(position: Int): Fragment =
        // Return a NEW fragment instance in createFragment(int)
        when (position) {
            0 -> DeviceListFragment().also { it.viewModel = viewModel }
            1 -> MachineListFragment().also { it.viewModel = viewModel }
            2 -> LinkListFragment().also { it.viewModel = viewModel }
            else -> DemoObjectFragment()
        }


//        fragment.arguments = Bundle().apply {
//            // Our object is just an integer :-P
//            putInt(ARG_ID, position)
//            putString(ARG_OBJECT, Labels[position])
//        }
//        return fragment

}


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var commandCollectionAdapter: DemoCollectionAdapter
    private lateinit var device_recycler: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding =
            DataBindingUtil.inflate<MainBinding>(inflater, R.layout.main_fragment, container, false)
        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val view: View = binding.root
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        // trying here
//        device_recycler = view.findViewById(R.id.device_recycler)
//        val device_list_adapter = DeviceListAdapter(viewModel)
//        device_recycler.layoutManager = LinearLayoutManager(context)
//        device_recycler.adapter = device_list_adapter

        // for the viewpager
        commandCollectionAdapter = DemoCollectionAdapter(this)
        commandCollectionAdapter.viewModel = viewModel

        // to update the machines
        val handler = Handler()
        val delay = 4000L // 4000 milliseconds == 4 second
        handler.postDelayed(object : Runnable {
            override fun run() {
                println("myHandler: here!") // Do your work here
                viewModel.machine_collector(viewModel.session)
                handler.postDelayed(this, delay)
            }
        }, delay)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.tab_pager)
        viewPager.adapter = commandCollectionAdapter

        tabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = Labels[position]
        }.attach()

    }

}

class DeviceListFragment : Fragment() {

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<SubBindingDevices>(
            inflater,
            R.layout.fragment_tab_devices,
            container,
            false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val device_list_adapter = DeviceListAdapter(viewModel)
        device_list_adapter.setDevices(viewModel.list_devices_l_t.value!!)
        device_recycler.layoutManager = LinearLayoutManager(context)
        device_recycler.adapter = device_list_adapter

        fab_device_rescan.setOnClickListener {
            Toast.makeText(context, "Rescan Queued", Toast.LENGTH_LONG).show()
            viewModel.device_rescan()
        }

//        viewModel.list_devices_l_t.observe(this, Observer {
//            log {"observed change $it.length"}
//        })

    }
}

class MachineListFragment : Fragment() {

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<SubBindingMachines>(
            inflater,
            R.layout.fragment_tab_machines,
            container,
            false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val device_list_adapter = MachineListAdapter(viewModel)
        device_list_adapter.setMachines(viewModel.list_machined_l_t.value!!)
        machine_recycler.layoutManager = LinearLayoutManager(context)
        machine_recycler.adapter = device_list_adapter
    }
}

class LinkListFragment : Fragment() {

    lateinit var viewModel: MainViewModel
    var fab_expanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<SubBindingLinks>(
            inflater,
            R.layout.fragment_tab_links,
            container,
            false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val device_list_adapter = LinkListAdapter(viewModel)
        device_list_adapter.setLinks(viewModel.list_links.values.toList())
        link_recycler.layoutManager = LinearLayoutManager(context)
        link_recycler.adapter = device_list_adapter

        fab_link_new.setOnClickListener {
            if (fab_expanded) {
                collapseFabs()
            } else {
                expandFabs()
            }
        }

        layoutFabAdd.setOnClickListener {
            val link_avail_src: Map<String, LinkSrc> = viewModel.list_srcs.map {
                it.list_name to it
            }.toMap()
            val link_avail_tgt: Map<String, LinkSink> = viewModel.list_sinks.map {
                it.list_name to it
            }.toMap()


            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_link_add, null)


            val link_source_adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                view.context,
                android.R.layout.simple_spinner_dropdown_item,
                viewModel.list_srcs.map {
                    it.list_name
                }
            )
            val link_target_adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                view.context,
                android.R.layout.simple_spinner_dropdown_item,
                viewModel.list_sinks.map {
                    it.list_name
                }
            )

            val link_source: Spinner = dialogView.findViewById(R.id.link_source)!!
            link_source.adapter = link_source_adapter

            val link_target: Spinner = dialogView.findViewById(R.id.link_target)!!
            link_target.adapter = link_target_adapter


            builder.setTitle(R.string.dialog_title_link_add)
            builder.apply {
                setView(dialogView)
                setPositiveButton(R.string.app_submit, null)
            }

            val link_name_textbox: EditText = dialogView.findViewById(R.id.link_name_textbox)
            val link_name_query: ImageButton = dialogView.findViewById(R.id.link_name_query)
            link_name_query.setOnClickListener {
                viewModel.query_and_set_names(link_name_textbox)
            }

            val dialog: AlertDialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val text: EditText = dialogView.findViewById(R.id.link_name_textbox)!!
                    if (text.text.length > 0)  {
                        viewModel.link_mk(
                            src = link_avail_src.get(link_source.getSelectedItem().toString())!!,
                            tgt = link_avail_tgt.get(link_target.getSelectedItem().toString())!!,
                            name = text.text.toString()
                        )
                        dialog.dismiss()
                    } else {
                        viewModel.query_and_set_names(text)

                    }
                }
            }
            dialog.show()
        }

        layoutFabBulk.setOnClickListener {
            val link_avail_src: Map<String, LinkSrc> = viewModel.list_srcs.map {
                it.list_name to it
            }.toMap()
            val link_avail_tgt: Map<String, LinkSink> = viewModel.list_sinks.map {
                it.list_name to it
            }.toMap()

            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_link_add_bulk, null)


            val link_source_adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                view.context,
                android.R.layout.simple_spinner_dropdown_item,
                viewModel.list_srcs.map {
                    it.list_name
                }
            )
            val link_source: Spinner = dialogView.findViewById(R.id.link_source_bulk)!!
            link_source.adapter = link_source_adapter


            builder.setTitle(R.string.dialog_title_link_add_bulk)
            builder.apply {
                setView(dialogView)
                setPositiveButton(R.string.app_submit, null)
            }
            val link_name_textbox: EditText = dialogView.findViewById(R.id.link_name_bulk_textbox)

            val link_name_query: ImageButton = dialogView.findViewById(R.id.link_name_bulk_query)
            link_name_query.setOnClickListener {
                viewModel.query_and_set_names(link_name_textbox)
            }

            val link_tgt_bulk: LinearLayout = dialogView.findViewById(R.id.link_tgt_bulk)
            var selected_links = mutableSetOf<String>()
            for (sink in viewModel.list_sinks.sortedBy { it.list_name }) {
                var cb = CheckBox(view.context)
                cb.text = sink.list_name
                cb.isChecked = false
                cb.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        selected_links.add(cb.text.toString())
                    } else {
                        selected_links.remove(cb.text.toString())
                    }
                }
                link_tgt_bulk.addView(cb)
            }


            val dialog: AlertDialog = builder.create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val text: EditText = dialogView.findViewById(R.id.link_name_bulk_textbox)!!
                    if (text.text.length > 0)  {
                        viewModel.link_mk_bulk(
                            src = link_avail_src.get(link_source.getSelectedItem().toString())!!,
                            tgt = selected_links.map {link_avail_tgt.get(it)!!},
                            name = text.text.toString()
                        )
                        dialog.dismiss()
                    } else {
                        viewModel.query_and_set_names(text)

                    }
                }
            }
            dialog.show()
        }

        layoutFabNuke.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            builder
                .setMessage(R.string.dialog_title_link_nuke_detail)
                .setTitle(R.string.dialog_title_link_nuke)
                .setPositiveButton(
                    R.string.app_okay,
                    DialogInterface.OnClickListener { dialog, id ->
                        viewModel.link_rm_bulk()
                        log { "submitted" }
                    })
                .setNegativeButton(
                    R.string.app_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    }
                )
            builder.create()
            builder.show()
        }

        collapseFabs()

    }

    fun expandFabs() {
        fab_expanded = true
        layoutFabNuke.visibility = View.VISIBLE
        layoutFabBulk.visibility = View.VISIBLE
        layoutFabAdd.visibility = View.VISIBLE
//        fab_link_new.setImageResource(R.drawable.ic_baseline_close_24)
        ViewCompat.animate(fab_link_new).rotation(45.0F).setDuration(300L)
            .setInterpolator(OvershootInterpolator(10.0F))
            .start();
    }

    fun collapseFabs() {
        fab_expanded = false
        layoutFabNuke.visibility = View.INVISIBLE
        layoutFabBulk.visibility = View.INVISIBLE
        layoutFabAdd.visibility = View.INVISIBLE
//        fab_link_new.setImageResource(R.drawable.ic_baseline_add_24)
        ViewCompat.animate(fab_link_new).rotation(0.0F).setDuration(300L)
            .setInterpolator(OvershootInterpolator(10.0F))
            .start();
    }
}


class DemoObjectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tab_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(android.R.id.text1)
            textView.text = getString(ARG_OBJECT).toString()
        }
    }
}
