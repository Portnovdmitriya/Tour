package com.example.tourguideplus.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.databinding.FragmentPlaceDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaceDetailFragment : Fragment() {
    private var currentPlace: PlaceEntity? = null
    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaceViewModel

    // Диалог прогресса для Wikipedia
    private var wikiProgressDialog: AlertDialog? = null

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

        // Кнопка удаления
        binding.btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удаление")
                .setMessage("Вы уверены, что хотите удалить «${place.name}»?")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Удалить") { _, _ ->
                    viewModel.deletePlace(place)
                    findNavController().popBackStack(R.id.placesFragment, false)
                }
                .show()
        }

        // Избранное
        binding.btnFavorite.text = if (place.isFavorite)
            "Убрать из избранного"
        else
            "Добавить в избранное"

        binding.btnFavorite.setOnClickListener {
            val updated = place.copy(isFavorite = !place.isFavorite)
            viewModel.updatePlace(updated)
            bindPlace(updated)
        }

        // Показать на карте (веб-версия)
        binding.btnMap.setOnClickListener {
            place.name.let { name ->
                val query = Uri.encode(name)
                val url = "https://www.google.com/maps/search/?api=1&query=$query"
                val chooser = Intent.createChooser(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .addCategory(Intent.CATEGORY_BROWSABLE),
                    "Открыть в браузере"
                )
                startActivity(chooser)
            }
        }

        // Википедия
        binding.btnWiki.setOnClickListener {
            place.name.let { name ->
                // Показать диалог прогресса
                wikiProgressDialog = MaterialAlertDialogBuilder(requireContext())
                    .setView(ProgressBar(requireContext()).apply {
                        isIndeterminate = true
                        setPadding(50, 50, 50, 50)
                    })
                    .setCancelable(false)
                    .show()

                // Запустить загрузку
                viewModel.loadWikiSummary(name)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем ViewModel
        val factory = PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        viewModel = ViewModelProvider(this, factory)[PlaceViewModel::class.java]

        // Читаем аргумент
        val placeId = arguments?.getLong("placeId") ?: return
        viewModel.loadPlaceById(placeId)

        // Наблюдаем данные места
        viewModel.selectedPlace.observe(viewLifecycleOwner) { place ->
            place?.let { bindPlace(it) }
        }

        // Наблюдаем результат Википедии
        viewModel.wikiExtract.observe(viewLifecycleOwner) { extract ->
            // Скрываем диалог прогресса
            wikiProgressDialog?.dismiss()
            wikiProgressDialog = null

            if (extract != null) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(currentPlace?.name)
                    .setMessage(extract)
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Не удалось получить данные из Википедии",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
