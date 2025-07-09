// app/src/main/java/com/example/tourguideplus/ui/routes/AddEditRouteDialogFragment.kt
package com.example.tourguideplus.ui.routes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.databinding.DialogAddEditRouteBinding
import com.example.tourguideplus.ui.main.PlaceViewModel
import com.example.tourguideplus.ui.main.PlaceViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddEditRouteDialogFragment : DialogFragment(R.layout.dialog_add_edit_route) {

    private var _binding: DialogAddEditRouteBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: RouteViewModel
    private lateinit var adapter: SelectablePlaceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = DialogAddEditRouteBinding.bind(view)

        // ViewModel
        vm = ViewModelProvider(
            this,
            RouteViewModelFactory(requireActivity().application as TourGuideApp)
        )[RouteViewModel::class.java]

        // Получаем все места из PlaceViewModel
        val placeVm = ViewModelProvider(
            requireActivity(),
            PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        )[PlaceViewModel::class.java]

        // Подписываемся на список мест
        placeVm.places.observe(viewLifecycleOwner) { places ->
            adapter = SelectablePlaceAdapter(places)
            binding.rvPlacesSelect.layoutManager = LinearLayoutManager(requireContext())
            binding.rvPlacesSelect.adapter = adapter
        }

        // Кнопка Сохранить
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            if (name.isEmpty()) {
                binding.etName.error = "Введите название"
                return@setOnClickListener
            }
            val desc = binding.etDesc.text.toString().trim().ifEmpty { null }
            val selectedIds = adapter.getSelectedPlaceIds()
            if (selectedIds.isEmpty()) {

                return@setOnClickListener
            }

            vm.createRoute(binding.etName.text.toString().trim(),
                binding.etDesc.text.toString().trim().ifEmpty { null },
                adapter.getSelectedPlaceIds())
        }

        // Отмена
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
