package com.example.tourguideplus.ui.main

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.CategoryEntity
import com.example.tourguideplus.databinding.DialogAddEditCategoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddEditCategoryDialogFragment(
    private val existing: CategoryEntity? = null
) : DialogFragment() {

    private var _b: DialogAddEditCategoryBinding? = null
    private val b get() = _b!!
    private lateinit var vm: CategoryViewModel

    override fun onCreateDialog(saved: Bundle?): Dialog {
        _b = DialogAddEditCategoryBinding.inflate(layoutInflater)
        val app = requireActivity().application as TourGuideApp
        vm = ViewModelProvider(this, CategoryViewModelFactory(app))
            .get(CategoryViewModel::class.java)

        existing?.let { b.etName.setText(it.name) }

        b.btnSave.setOnClickListener {
            val name = b.etName.text.toString().trim()
            if (name.isEmpty()) {
                b.etName.error = "Введите имя"
            } else {
                if (existing == null) vm.addCategory(name)
                else vm.updateCategory(existing.copy(name = name))
                dismiss()
            }
        }
        b.btnCancel.setOnClickListener { dismiss() }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(b.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
