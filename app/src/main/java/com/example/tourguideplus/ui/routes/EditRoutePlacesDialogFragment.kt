package com.example.tourguideplus.ui.routes

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.databinding.DialogEditRoutePlacesBinding
import com.example.tourguideplus.ui.main.PlaceViewModel
import com.example.tourguideplus.ui.main.PlaceViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class EditRoutePlacesDialogFragment(
    private val routeId: Long
) : DialogFragment() {

    private var _b: DialogEditRoutePlacesBinding? = null
    private val b get() = _b!!

    private lateinit var routeVm: RouteViewModel
    private lateinit var placeVm: PlaceViewModel

    private var adapter: SelectablePlaceAdapter? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _b = DialogEditRoutePlacesBinding.inflate(layoutInflater)

        val app = requireActivity().application as TourGuideApp
        routeVm = ViewModelProvider(this, RouteViewModelFactory(app))[RouteViewModel::class.java]
        placeVm = ViewModelProvider(this, PlaceViewModelFactory(app))[PlaceViewModel::class.java]

        b.rvPlacesSelect.layoutManager = LinearLayoutManager(requireContext())

        // 1) Берём текущий маршрут (ради предвыбора)
        routeVm.select(routeId)
        routeVm.selected.observe(this) { rwp ->
            if (rwp == null) return@observe

            val selectedIds = rwp.places.map { it.id }.toSet()

            // 2) Подгружаем список всех мест — и строим адаптер с предвыбором
            placeVm.places.observe(this) { allPlaces ->
                adapter = SelectablePlaceAdapter(allPlaces, initialSelectedIds = selectedIds)
                b.rvPlacesSelect.adapter = adapter
            }

            // Кнопки
            b.btnCancel.setOnClickListener { dismiss() }
            b.btnSave.setOnClickListener {
                val newIds = adapter?.getSelectedPlaceIds().orEmpty()
                // Обновляем только связки, имя/описание оставляем прежними
                val updatedRoute = RouteEntity(
                    id = rwp.route.id,
                    name = rwp.route.name,
                    description = rwp.route.description
                )
                lifecycleScope.launch {
                    routeVm.updateRoute(updatedRoute, newIds)
                    dismiss() // thanks to reactive VM, экран обновится сразу
                }
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(b.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
