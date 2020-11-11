package io.gmp.does.lambent.droid.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.gmp.does.lambent.droid.DeviceListAdapter
import io.gmp.does.lambent.droid.MainBinding
import io.gmp.does.lambent.droid.R
import io.gmp.does.lambent.droid.SubBindingDevices
import kotlinx.android.synthetic.main.fragment_tab_devices.*

val Labels = listOf<String>(
    "Devices",
    "Machines",
    "Links",
    "Sets"
)

private const val ARG_ID = "id"
private const val ARG_OBJECT = "object"

class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = Labels.size
//    lateinit var fragment: Fragment

    override fun createFragment(position: Int): Fragment =
        // Return a NEW fragment instance in createFragment(int)
        when (position) {
            0 ->  DeviceListFragment()
            1 ->  DeviceListFragment()
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<MainBinding>(inflater, R.layout.main_fragment, container, false)
        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val view: View = binding.root
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commandCollectionAdapter = DemoCollectionAdapter(this)
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
        val binding = DataBindingUtil.inflate<SubBindingDevices>(inflater, R.layout.fragment_tab_devices, container, false)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val view: View = binding.root
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val device_list_adapter = DeviceListAdapter()
        device_list_adapter.setPlaces(viewModel.list_devices.values.toList())
        device_recycler.layoutManager = LinearLayoutManager(context)
        device_recycler.adapter = device_list_adapter
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
