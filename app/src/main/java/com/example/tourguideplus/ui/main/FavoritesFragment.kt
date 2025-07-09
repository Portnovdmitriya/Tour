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
import com.example.tourguideplus.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaceViewModel
    private lateinit var adapter: PlaceWithCategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) ViewModel
        val factory = PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        viewModel = ViewModelProvider(this, factory)
            .get(PlaceViewModel::class.java)

        // 2) Адаптер PlaceWithCategoriesAdapter
        adapter = PlaceWithCategoriesAdapter { pwc: PlaceWithCategories ->
            // навигация в детали: передаём pwc.place.id
            val bundle = bundleOf("placeId" to pwc.place.id)
            findNavController().navigate(
                R.id.action_global_navigation_favorites_to_placeDetailFragment,
                bundle
            )
        }

        // 3) RecyclerView
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // 4) Подписываемся на LiveData мест с категориями и фильтруем избранное
        viewModel.placesWithCategories.observe(viewLifecycleOwner) { list ->
            val favs = list.filter { it.place.isFavorite }
            adapter.submitList(favs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
