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
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaceViewModel
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем ViewModel
        val factory = PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        viewModel = ViewModelProvider(this, factory)
            .get(PlaceViewModel::class.java)

        // Адаптер точно такой же
        adapter = PlaceAdapter { place ->
            // навигация в детали
            val bundle = bundleOf("placeId" to place.id)
            findNavController().navigate(
                R.id.action_global_navigation_favorites_to_placeDetailFragment,
                bundle
            )
        }

        // RecyclerView
        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Наблюдаем все места, фильтруем избранное
        viewModel.places.observe(viewLifecycleOwner) { list ->
            val favs = list.filter { it.isFavorite }
            adapter.submitList(favs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
