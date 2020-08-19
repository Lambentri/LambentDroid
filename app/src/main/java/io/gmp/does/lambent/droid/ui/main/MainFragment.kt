package io.gmp.does.lambent.droid.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import io.gmp.does.lambent.droid.MainBinding
import io.gmp.does.lambent.droid.R
import io.gmp.does.lambent.droid.log

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

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

}