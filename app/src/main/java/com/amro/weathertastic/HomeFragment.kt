package com.amro.weathertastic

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amro.weathertastic.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = HomeFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.fetchDailyData()
        viewModel.dailyWeatherData.observe(viewLifecycleOwner, Observer {
            binding.textView.text = it.toString()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}