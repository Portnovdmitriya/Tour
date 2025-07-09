package com.example.tourguideplus.ui.main

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.databinding.DialogAddEditPlaceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class AddEditPlaceDialogFragment(
    private val existingPlace: PlaceEntity? = null
) : DialogFragment() {

    private var _binding: DialogAddEditPlaceBinding? = null
    private val binding get() = _binding!!

    private lateinit var placeVm: PlaceViewModel
    private lateinit var categoryVm: CategoryViewModel
    private lateinit var categoryAdapter: SelectableCategoryAdapter

    private var photoUri: Uri? = null

    companion object {
        private const val REQUEST_PICK_IMAGE = 1001
        private const val REQUEST_TAKE_PHOTO = 1002
    }

    // Лаунчер для CAMERA permission
    private val requestCameraPermission =
        registerForActivityResult(RequestPermission()) { granted ->
            if (granted) takePhotoInternal()
            else Toast.makeText(
                requireContext(),
                "Без доступа к камере сделать фото нельзя",
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditPlaceBinding.inflate(layoutInflater)


        val app = requireActivity().application as TourGuideApp
        placeVm = ViewModelProvider(this, PlaceViewModelFactory(app))
            .get(PlaceViewModel::class.java)
        categoryVm = ViewModelProvider(this, CategoryViewModelFactory(app))
            .get(CategoryViewModel::class.java)

        existingPlace?.let {
            binding.etName.setText(it.name)
            binding.etDescription.setText(it.description)
            it.photoUri?.let { uriStr ->
                photoUri = Uri.parse(uriStr)
                binding.ivPhotoPreview.setImageURI(photoUri)
            }
        }


        categoryAdapter = SelectableCategoryAdapter(emptyList())
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = categoryAdapter

        categoryVm.allCategories.observe(this) { cats ->
            categoryAdapter = SelectableCategoryAdapter(cats)
            binding.rvCategories.adapter = categoryAdapter
        }


        binding.btnAddCategory.setOnClickListener {
            val newName = binding.etNewCategory.text.toString().trim()
            if (newName.isEmpty()) {
                binding.etNewCategory.error = "Введите название категории"
            } else {
                categoryVm.addCategory(newName)
                binding.etNewCategory.text?.clear()
            }
        }


        binding.btnChoosePhoto.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Добавить фото")
                .setItems(arrayOf("Из галереи", "С камеры")) { _, which ->
                    if (which == 0) pickFromGallery() else onCameraSelected()
                }
                .show()
        }


        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            if (name.isEmpty()) {
                binding.etName.error = "Введите название"
                return@setOnClickListener
            }
            // Собираем PlaceEntity (поле category больше не используется)
            val place = PlaceEntity(
                id          = existingPlace?.id ?: 0L,
                name        = name,
                category    = "",
                description = desc,
                latitude    = existingPlace?.latitude,
                longitude   = existingPlace?.longitude,
                photoUri    = photoUri?.toString()
            )
            // Собираем выбранные категории
            val selIds = categoryAdapter.getSelectedIds()
            if (selIds.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Выберите хотя бы одну категорию", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (existingPlace == null) {
                placeVm.createPlaceWithCategories(place, selIds)
            } else {
                placeVm.updatePlaceWithCategories(place.copy(id = existingPlace.id), selIds)
            }
            dismiss()
        }

        // Отмена
        binding.btnCancel.setOnClickListener { dismiss() }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun pickFromGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply { addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun onCameraSelected() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> takePhotoInternal()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> MaterialAlertDialogBuilder(requireContext())
                .setTitle("Нужен доступ")
                .setMessage("Разрешите камеру для создания фото")
                .setPositiveButton("OK") { _, _ ->
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                }.show()
            else -> requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun takePhotoInternal() {
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(dir, "IMG_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        val cam = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        startActivityForResult(cam, REQUEST_TAKE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_IMAGE -> data?.data?.let { uri ->
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    photoUri = uri
                }
                REQUEST_TAKE_PHOTO -> { /* photoUri уже установлен */ }
            }
            binding.ivPhotoPreview.setImageURI(photoUri)
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
