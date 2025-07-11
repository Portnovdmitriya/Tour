package com.example.tourguideplus.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.databinding.FragmentPlaceDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaceDetailFragment : Fragment() {

    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaceViewModel
    private var currentPlace: PlaceEntity? = null
    private var placeId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        val app = requireActivity().application as TourGuideApp
        viewModel = ViewModelProvider(this, PlaceViewModelFactory(app))
            .get(PlaceViewModel::class.java)

        // Из аргументов
        placeId = arguments?.getLong("placeId") ?: return

        // Загрузка самого места
        viewModel.loadPlaceById(placeId)

        // Когда место загрузилось — заполняем UI
        viewModel.selectedPlace.observe(viewLifecycleOwner) { place ->
            place ?: return@observe
            currentPlace = place

            binding.tvName.text = place.name
            binding.tvDescription.text = place.description
            place.photoUri?.let { uriStr ->
                binding.ivPhoto.setImageURI(Uri.parse(uriStr))
            }
        }

        // Наблюдаем за списком избранных ID, чтобы прописать текст кнопки
        viewModel.favoriteIds.observe(viewLifecycleOwner) { favs ->
            val isFav = favs.contains(placeId)
            binding.btnFavorite.text =
                if (isFav) "Убрать из избранного" else "Добавить в избранное"
        }

        // Переключаем «избранное»
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite(placeId)
        }

        // Удаление места
        binding.btnDelete.setOnClickListener {
            currentPlace?.let { place ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Удаление места")
                    .setMessage("Вы уверены, что хотите удалить «${place.name}»?")
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Удалить") { _, _ ->
                        viewModel.deletePlace(place)
                        findNavController().popBackStack()
                    }
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
