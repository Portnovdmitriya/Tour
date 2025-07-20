package com.example.tourguideplus.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.databinding.FragmentPlaceDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaceDetailFragment : Fragment() {

    private var _b: FragmentPlaceDetailBinding? = null
    private val b get() = _b!!

    private lateinit var vm: PlaceViewModel
    private var current: PlaceEntity? = null
    private var placeId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        .also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as TourGuideApp
        vm = ViewModelProvider(this, PlaceViewModelFactory(app))
            .get(PlaceViewModel::class.java)

        placeId = arguments?.getLong("placeId") ?: return
        vm.loadPlaceById(placeId)

        // Подписываемся на место
        vm.selectedPlace.observe(viewLifecycleOwner) { place ->
            place ?: return@observe
            current = place
            b.tvName.text = place.name
            b.tvDescription.text = place.description
            place.photoUri?.let { b.ivPhoto.setImageURI(Uri.parse(it)) }
        }

        // Кнопка «Избранное»
        vm.favoriteIds.observe(viewLifecycleOwner) { favs ->
            val isFav = favs.contains(placeId)
            b.btnFavorite.text =
                if (isFav) "Убрать из избранного" else "Добавить в избранное"
        }
        b.btnFavorite.setOnClickListener {
            vm.toggleFavorite(placeId)
        }

        // Удаление (уже было)
        b.btnDelete.setOnClickListener {
            current?.let { place ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Удаление места")
                    .setMessage("Вы уверены, что хотите удалить «${place.name}»?")
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Удалить") { _, _ ->
                        vm.deletePlace(place)
                        findNavController().popBackStack()
                    }
                    .show()
            }
        }

        // Карта
        b.btnMap.setOnClickListener {
            current?.let { place ->
                val query = Uri.encode(place.name)
                val url = "https://www.google.com/maps/search/?api=1&query=$query"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                startActivity(Intent.createChooser(intent, "Открыть в браузере"))
            }
        }

        // Википедия
        b.btnWiki.setOnClickListener {
            current?.let { place ->
                // прогресс
                val dlg = MaterialAlertDialogBuilder(requireContext())
                    .setView(ProgressBar(requireContext()).apply {
                        isIndeterminate = true
                        setPadding(50,50,50,50)
                    })
                    .setCancelable(false)
                    .show()

                vm.loadWikiSummary(place.name)
                vm.wikiExtract.observe(viewLifecycleOwner) { extract ->
                    // убираем прогресс
                    dlg.dismiss()
                    if (!extract.isNullOrEmpty()) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(place.name)
                            .setMessage(extract)
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        Toast.makeText(requireContext(),
                            "Не удалось получить данные из Википедии",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
