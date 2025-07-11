package com.example.tourguideplus.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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

        // Инициализируем ViewModel
        val app = requireActivity().application as TourGuideApp
        viewModel = ViewModelProvider(
            this,
            PlaceViewModelFactory(app)
        )[PlaceViewModel::class.java]

        // Настраиваем адаптер PlaceWithCategoriesAdapter
        adapter = PlaceWithCategoriesAdapter { pwc: PlaceWithCategories ->
            // При клике передаём в аргументы ID места
            val args = bundleOf("placeId" to pwc.place.id)
            findNavController().navigate(
                R.id.action_placesFragment_to_placeDetailFragment,
                args
            )
        }

        // Настраиваем RecyclerView
        binding.rvPlaces.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaces.adapter = adapter
        binding.rvPlaces.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Подписываемся на LiveData мест с категориями
        viewModel.placesWithCategories.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
