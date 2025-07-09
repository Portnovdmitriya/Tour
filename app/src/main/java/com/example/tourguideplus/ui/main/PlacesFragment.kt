package com.example.tourguideplus.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.os.bundleOf
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceWithCategories
import com.example.tourguideplus.databinding.FragmentPlacesBinding

class PlacesFragment : Fragment() {

    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaceViewModel
    private lateinit var adapter: PlaceWithCategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Инициализируем ViewModel
        val factory = PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        viewModel = ViewModelProvider(this, factory)
            .get(PlaceViewModel::class.java)

        // 2) Настраиваем адаптер PlaceWithCategoriesAdapter
        adapter = PlaceWithCategoriesAdapter { pwc: PlaceWithCategories ->
            // pwc.place — это ваша PlaceEntity
            val bundle = bundleOf("placeId" to pwc.place.id)
            findNavController().navigate(
                R.id.action_placesFragment_to_placeDetailFragment,
                bundle
            )
        }

        // 3) RecyclerView
        binding.rvPlaces.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaces.adapter = adapter
        binding.rvPlaces.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // 4) Подписываемся на LiveData с местами и их категориями
        viewModel.placesWithCategories.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
