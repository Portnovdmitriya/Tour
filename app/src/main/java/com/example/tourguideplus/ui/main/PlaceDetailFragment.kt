package com.example.tourguideplus.ui.main

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.databinding.FragmentPlaceDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.navigation.fragment.findNavController
import android.content.Intent
import android.widget.Toast
import com.example.tourguideplus.R

class PlaceDetailFragment : Fragment() {
    private var currentPlace: PlaceEntity? = null
    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!

    // Объявляем viewModel
    private lateinit var viewModel: PlaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun bindPlace(place: PlaceEntity) {
        currentPlace = place

        binding.tvName.text        = place.name
        binding.tvCategory.text    = place.category
        binding.tvDescription.text = place.description
        place.photoUri?.let { uriStr ->
            try { binding.ivPhoto.setImageURI(Uri.parse(uriStr)) }
            catch (_: Exception) {}
        }
// Обработчик удаления
        binding.btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удаление")
                .setMessage("Вы уверены, что хотите удалить «${place.name}»?")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Удалить") { _, _ ->
                    viewModel.deletePlace(place)
                    // возвращаемся к списку
                    findNavController().popBackStack(R.id.placesFragment, false)
                }
                .show()
        }
        //  «избранное»
        binding.btnFavorite.text = if (place.isFavorite)
            "Убрать из избранного"
        else
            "Добавить в избранное"

        // Обработчик «избранного»
        binding.btnFavorite.setOnClickListener {
            val updated = place.copy(isFavorite = !place.isFavorite)
            viewModel.updatePlace(updated)
            bindPlace(updated)
        }
        binding.btnMap.setOnClickListener {
            currentPlace?.let { place ->
                // Кодируем название для URL
                val query = Uri.encode(place.name)
                // Собираем ссылку на веб-версию Google Maps
                val url = "https://www.google.com/maps/search/?api=1&query=$query"
                // Готовим Intent для открытия ссылки
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    // Указываем, что нам нужен браузер
                    addCategory(Intent.CATEGORY_BROWSABLE)
                }
                // Показываем диалог выбора приложения
                val chooser = Intent.createChooser(intent, "Открыть в браузере")
                startActivity(chooser)
            }
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
