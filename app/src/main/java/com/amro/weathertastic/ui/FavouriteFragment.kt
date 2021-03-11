package com.amro.weathertastic.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.FavouriteFragmentBinding
import com.amro.weathertastic.utils.Constants
import com.amro.weathertastic.viewModel.FavouriteViewModel
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import java.math.BigDecimal
import java.math.RoundingMode

class FavouriteFragment : Fragment() {

    private lateinit var viewModel: FavouriteViewModel
    private var _binding: FavouriteFragmentBinding? = null
    private val binding get() = _binding!!
    private var transaction : FragmentTransaction? = null
    private lateinit var favouriteAdapter: FavouriteRecyclerAdaptor


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavouriteFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)
        favouriteAdapter = FavouriteRecyclerAdaptor(ArrayList(),viewModel)
        initRecyclers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        favouriteAdapter.viewModel = viewModel
        // TODO: Use the ViewModel
        Log.i(Constants.LOG_TAG,"in livedata2222")
        viewModel.fetchFavouriteList("0","0").observe(viewLifecycleOwner, {
            Log.i(Constants.LOG_TAG,"in livedata")
                if(it != null){
                    if(!it.isEmpty()){
                        Log.i(Constants.LOG_TAG,"in")
                        //binding.textView3.text = it.size.toString()
                        favouriteAdapter.setIncomingList(it.reversed())
                        binding.backgroundNoData.visibility = View.GONE
                    }else{
                     binding.backgroundNoData.visibility = View.VISIBLE
                    }
                }else{
                    binding.backgroundNoData.visibility = View.VISIBLE
                }
            })
        binding.favFloatingButton.setOnClickListener {
            binding.favFloatingButton.visibility = View.GONE
            showAutoCompleteBar()
        }
    }

    fun initRecyclers(){
        binding.favouriteRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.favouriteRecycler.adapter = favouriteAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAutoCompleteBar(){
        binding.searchFragmentContainer.visibility= View.VISIBLE
        val autocompleteFragment = PlaceAutocompleteFragment.newInstance(Constants.MAPBOX_API_KEY)
            transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.add(R.id.search_fragment_container, autocompleteFragment, Constants.AUTOCOMPLETE_FRAGMENT_TAG)
            transaction?.commit()

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {
                val lonDecimal = BigDecimal(carmenFeature.center()!!.longitude()).setScale(4, RoundingMode.HALF_DOWN)
                val latDecimal = BigDecimal(carmenFeature.center()!!.latitude()).setScale(4, RoundingMode.HALF_DOWN)
                Toast.makeText(context,"latitude $latDecimal \n longitude $lonDecimal", Toast.LENGTH_LONG).show()
                viewModel.fetchFavouriteList(latDecimal.toString(),lonDecimal.toString())
                activity?.supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
                binding.favFloatingButton.visibility = View.VISIBLE
            }

            override fun onCancel() {
                Log.i(Constants.LOG_TAG,"cancel")
                activity?.supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
                binding.favFloatingButton.visibility = View.VISIBLE
            }
        })
    }

}