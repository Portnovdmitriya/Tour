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

        // ViewModel
        val app = requireActivity().application as TourGuideApp
        viewModel = ViewModelProvider(this, PlaceViewModelFactory(app))
            .get(PlaceViewModel::class.java)

        // Адаптер
        adapter = PlaceWithCategoriesAdapter { pwc: PlaceWithCategories ->
            val bundle = bundleOf("placeId" to pwc.place.id)
            // Навигация напрямую по ID фрагмента деталей
            findNavController().navigate(R.id.placeDetailFragment, bundle)
        }

        // RecyclerView
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Подписываемся на favoritePlacesWithCategories вместо чистого placesWithCategories
        viewModel.favoritePlacesWithCategories.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
