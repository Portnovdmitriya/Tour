package com.example.tourguideplus.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем ViewModel
        val app = requireActivity().application as TourGuideApp
        viewModel = ViewModelProvider(this, PlaceViewModelFactory(app))
            .get(PlaceViewModel::class.java)

        // Получаем placeId из аргументов
        val placeId = arguments?.getLong("placeId") ?: return

        // Загружаем PlaceEntity
        viewModel.loadPlaceById(placeId)

        // Наблюдаем за выбранным местом
        viewModel.selectedPlace.observe(viewLifecycleOwner) { place ->
            place ?: return@observe
            currentPlace = place

            // Заполняем UI
            binding.tvName.text        = place.name
            binding.tvCategory.text    = place.category   // если не используете категории, можно убрать
            binding.tvDescription.text = place.description
            place.photoUri?.let { uriStr ->
                binding.ivPhoto.setImageURI(Uri.parse(uriStr))
            }

            // Обновляем текст кнопки «Избранное»
            binding.btnFavorite.text = if (place.isFavorite)
                "Убрать из избранного"
            else
                "Добавить в избранное"
        }

        // Обработчик «Избранное»
        binding.btnFavorite.setOnClickListener {
            currentPlace?.let { place ->
                val updated = place.copy(isFavorite = !place.isFavorite)
                viewModel.updatePlace(updated)
            }
        }

        // Обработчик «Удалить»
        binding.btnDelete.setOnClickListener {
            currentPlace?.let { place ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Удаление места")
                    .setMessage("Вы уверены, что хотите удалить «${place.name}»?")
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Удалить") { _, _ ->
                        viewModel.deletePlace(place)
                        // Возвращаемся назад к списку
                        findNavController().popBackStack()
                    }
                    .show()
            }
        }

        // Остальное (карта, Wikipedia) оставляем без изменений…
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
