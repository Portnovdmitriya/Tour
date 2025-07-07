package com.example.tourguideplus.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.databinding.FragmentPlaceDetailBinding

class PlaceDetailFragment : Fragment() {

    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!

    // <-- Объявляем viewModel
    private lateinit var viewModel: PlaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun bindPlace(place: PlaceEntity) {
        binding.tvName.text        = place.name
        binding.tvCategory.text    = place.category
        binding.tvDescription.text = place.description
        place.photoUri?.let { uriStr ->
            binding.ivPhoto.setImageURI(Uri.parse(uriStr))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем viewModel
        val factory = PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        viewModel = ViewModelProvider(this, factory)
            .get(PlaceViewModel::class.java)

        // Получаем ID из аргументов
        val placeId = arguments?.getLong("placeId") ?: return

        // Загружаем данные
        viewModel.loadPlaceById(placeId)

        // Наблюдаем за выбранным местом и биндим
        viewModel.selectedPlace.observe(viewLifecycleOwner) { place ->
            place?.let { bindPlace(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
