package com.example.tourguideplus.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.FragmentPlacesBinding
import androidx.core.os.bundleOf

class PlacesFragment : Fragment() {

    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaceViewModel
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // ViewModel
        val factory = PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        viewModel = ViewModelProvider(this, factory)
            .get(PlaceViewModel::class.java)

        // Adapter
        adapter = PlaceAdapter { place ->
            val bundle = bundleOf("placeId" to place.id)
            findNavController().navigate(
                R.id.action_placesFragment_to_placeDetailFragment,
                bundle
            )
        }

        // RecyclerView
        binding.rvPlaces.adapter = adapter
        binding.rvPlaces.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Наблюдаем данные
        viewModel.places.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
