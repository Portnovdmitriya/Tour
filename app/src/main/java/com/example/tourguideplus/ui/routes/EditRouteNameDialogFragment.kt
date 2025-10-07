package com.example.tourguideplus.ui.routes

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.databinding.DialogEditRouteNameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditRouteNameDialogFragment(
    private val routeId: Long,
    private val currentName: String,
    private val currentDesc: String?
) : DialogFragment() {

    private var _b: DialogEditRouteNameBinding? = null
    private val b get() = _b!!
    private lateinit var vm: RouteViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _b = DialogEditRouteNameBinding.inflate(layoutInflater)

        vm = ViewModelProvider(
            this,
            RouteViewModelFactory(requireActivity().application as TourGuideApp)
        )[RouteViewModel::class.java]

        // заполняем текущие значения
        b.etName.setText(currentName)
        b.etDesc.setText(currentDesc ?: "")

        b.btnCancel.setOnClickListener { dismiss() }
        b.btnSave.setOnClickListener {
            val newName = b.etName.text?.toString()?.trim().orEmpty()
            val newDesc = b.etDesc.text?.toString()?.trim()?.ifEmpty { null }

            if (newName.isEmpty()) {
                b.etName.error = "Введите название"
                return@setOnClickListener
            }

            // Берём текущий набор мест и обновляем только имя/описание
            vm.select(routeId) // на случай если selected ещё не загружен
            val current = vm.selected.value
            if (current != null) {
                val placeIds = current.places.map { it.id }
                vm.updateRoute(
                    RouteEntity(id = routeId, name = newName, description = newDesc),
                    placeIds
                )
                dismiss()
            } else {
                // если не успело загрузиться, подпишемся один раз
                vm.selected.observe(this) { rwp ->
                    if (rwp != null) {
                        val placeIds = rwp.places.map { it.id }
                        vm.updateRoute(
                            RouteEntity(id = routeId, name = newName, description = newDesc),
                            placeIds
                        )
                        dismiss()
                    }
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
