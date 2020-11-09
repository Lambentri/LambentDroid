package io.gmp.does.lambent.droid.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.gmp.does.lambent.droid.MainBinding
import io.gmp.does.lambent.droid.R


private const val ARG_OBJECT = "object"

class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 100

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = MainFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
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
//        return inflater.inflate(R.layout.main_fragment, container, false)
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

//        commandCollectionAdapter = DemoCollectionAdapter(this)
//        viewPager = view.findViewById(R.id.tab_pager)
//        viewPager.adapter = commandCollectionAdapter
//
//        tabLayout = view.findViewById(R.id.tab_layout)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = "OBJECT ${(position + 1)}"
//        }.attach()
    }

}