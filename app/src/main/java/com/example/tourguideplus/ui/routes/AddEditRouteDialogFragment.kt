package com.example.tourguideplus.ui.routes

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.databinding.DialogAddEditRouteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddEditRouteDialogFragment(
    private val existing: RouteEntity? = null
) : DialogFragment() {

    private var _binding: DialogAddEditRouteBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: RouteViewModel

    override fun onCreateDialog(saved: Bundle?) =
        Dialog(requireContext()).apply {
            _binding = DialogAddEditRouteBinding.inflate(layoutInflater)
            setContentView(binding.root)

            vm = ViewModelProvider(
                this@AddEditRouteDialogFragment,
                RouteViewModelFactory(requireActivity().application as TourGuideApp)
            )[RouteViewModel::class.java]

            existing?.let {
                binding.etName.setText(it.name)
                binding.etDesc.setText(it.description)
            }

            binding.btnSave.setOnClickListener {
                val name = binding.etName.text.toString().trim()
                if (name.isEmpty()) {
                    binding.etName.error = "Введите название"
                    return@setOnClickListener
                }
                val desc = binding.etDesc.text.toString().trim().ifEmpty { null }
                val route = RouteEntity(
                    id = existing?.id ?: 0L,
                    name = name,
                    description = desc
                )
                if (existing == null) vm.add(route)
                else vm.update(route)
                dismiss()
            }
            binding.btnCancel.setOnClickListener { dismiss() }
        }
}
