package com.example.tourguideplus.ui.routes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.DialogAddEditRouteBinding
import com.example.tourguideplus.ui.main.PlaceViewModel
import com.example.tourguideplus.ui.main.PlaceViewModelFactory

class AddEditRouteDialogFragment : DialogFragment(R.layout.dialog_add_edit_route) {

    private var _binding: DialogAddEditRouteBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: RouteViewModel
    private lateinit var adapter: SelectablePlaceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogAddEditRouteBinding.bind(view)

        // Инициализируем RouteViewModel
        vm = ViewModelProvider(
            this,
            RouteViewModelFactory(requireActivity().application as TourGuideApp)
        )[RouteViewModel::class.java]

        // Подписываемся на список всех мест из PlaceViewModel
        val placeVm = ViewModelProvider(
            requireActivity(),
            PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        )[PlaceViewModel::class.java]

        placeVm.places.observe(viewLifecycleOwner) { places ->
            adapter = SelectablePlaceAdapter(places)
            binding.rvPlacesSelect.layoutManager = LinearLayoutManager(requireContext())
            binding.rvPlacesSelect.adapter = adapter
        }

        // Обработчик кнопки «Сохранить»
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            if (name.isEmpty()) {
                binding.etName.error = "Введите название"
                return@setOnClickListener
            }

            val desc = binding.etDesc.text.toString().trim().ifEmpty { null }
            val selectedIds = adapter.getSelectedPlaceIds()
            if (selectedIds.isEmpty()) {
                binding.etName.error = "Выберите хотя бы одно место"
                return@setOnClickListener
            }

            // Создаём маршрут
            vm.createRoute(name, desc, selectedIds)
            // Закрываем диалог
            dismiss()
        }

        // Отмена
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
