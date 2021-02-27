package com.amro.weathertastic.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.amro.weathertastic.viewModel.FavouriteViewModel
import com.amro.weathertastic.R
import com.amro.weathertastic.databinding.FavouriteFragmentBinding
import com.amro.weathertastic.model.Constants
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener

class FavouriteFragment : Fragment() {

    private lateinit var viewModel: FavouriteViewModel
    private var _binding: FavouriteFragmentBinding? = null
    private val binding get() = _binding!!
    private var transaction : FragmentTransaction? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavouriteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)
        // TODO: Use the ViewModel

        binding.favFloatingButton.setOnClickListener {
            showAutoCompleteBar()
        }
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
                // TODO: Use the longitude and latitude
                Toast.makeText(context,"latitude ${carmenFeature.center()?.latitude()} \n longitude ${carmenFeature.center()?.longitude()}"
                    , Toast.LENGTH_LONG).show()
                activity?.supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
            }

            override fun onCancel() {
                Log.i(Constants.LOG_TAG,"cancel")
                activity?.supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
            }
        })
    }

}