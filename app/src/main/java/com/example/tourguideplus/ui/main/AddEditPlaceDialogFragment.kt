package com.example.tourguideplus.ui.main

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.databinding.DialogAddEditPlaceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddEditPlaceDialogFragment(
    private val existingPlace: PlaceEntity? = null
) : DialogFragment() {

    private var _binding: DialogAddEditPlaceBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaceViewModel
    private var photoUri: Uri? = null

    companion object {
        private const val REQUEST_PICK_IMAGE = 1001
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditPlaceBinding.inflate(layoutInflater)

        // ViewModel
        val factory = PlaceViewModelFactory(requireActivity().application as TourGuideApp)
        viewModel = ViewModelProvider(this, factory).get(PlaceViewModel::class.java)

        // Если редактируем – заполняем поля
        existingPlace?.let {
            binding.etName.setText(it.name)
            binding.etCategory.setText(it.category)
            binding.etDescription.setText(it.description)
            it.photoUri?.let { uriStr ->
                photoUri = Uri.parse(uriStr)
                binding.ivPhotoPreview.setImageURI(photoUri)
            }
        }

        // Выбор фото
        binding.btnChoosePhoto.setOnClickListener {
            startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                REQUEST_PICK_IMAGE
            )
        }

        // Сохранить
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val category = binding.etCategory.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = "Введите название"
                return@setOnClickListener
            }
            if (category.isEmpty()) {
                binding.etCategory.error = "Введите категорию"
                return@setOnClickListener
            }
            if (desc.isEmpty()) {
                binding.etDescription.error = "Введите описание"
                return@setOnClickListener
            }

            val place = PlaceEntity(
                id = existingPlace?.id ?: 0L,
                name = name,
                category = category,
                description = desc,
                latitude = existingPlace?.latitude,
                longitude = existingPlace?.longitude,
                photoUri = photoUri?.toString()
            )

            if (existingPlace == null) viewModel.addPlace(place)
            else viewModel.updatePlace(place)
            dismiss()
        }

        // Отмена
        binding.btnCancel.setOnClickListener { dismiss() }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // захватываем persistable разрешения
                val takeFlags = data.flags and
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)

                photoUri = uri
                binding.ivPhotoPreview.setImageURI(photoUri)
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
