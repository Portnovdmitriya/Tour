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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.data.model.PlaceWithCategories
import com.example.tourguideplus.databinding.DialogAddEditPlaceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File

class AddEditPlaceDialogFragment(
    private val existingPlace: PlaceEntity? = null
) : DialogFragment() {

    private lateinit var binding: DialogAddEditPlaceBinding
    private lateinit var placeVm: PlaceViewModel
    private lateinit var categoryVm: CategoryViewModel
    private lateinit var categoryAdapter: SelectableCategoryAdapter
    private var photoUri: Uri? = null

    companion object {
        private const val REQUEST_PICK_IMAGE = 1001
        private const val REQUEST_TAKE_PHOTO = 1002
    }

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
        // 1) Inflate binding
        binding = DialogAddEditPlaceBinding.inflate(layoutInflater)

        // 2) Инициализируем ViewModel-ы
        val app = requireActivity().application as TourGuideApp
        placeVm = ViewModelProvider(this, PlaceViewModelFactory(app))
            .get(PlaceViewModel::class.java)
        categoryVm = ViewModelProvider(this, CategoryViewModelFactory(app))
            .get(CategoryViewModel::class.java)

        // 3) Настраиваем RecyclerView для категорий
        categoryAdapter = SelectableCategoryAdapter { _, _ -> /* нет дополнительной логики */ }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        // 4) Подписываемся на список категорий
        categoryVm.allCategories.observe(this) { cats ->
            categoryAdapter.submitList(cats)
            // Если редактируем — выставляем уже связанные
            existingPlace?.let { place ->
                lifecycleScope.launch {
                    val pwc: PlaceWithCategories? =
                        categoryVm.getPlaceWithCategories(place.id)
                    categoryAdapter.setSelectedIds(
                        pwc?.categories?.map { it.id } ?: emptyList()
                    )
                }
            }
        }

        // 5) Заполняем поля, если это редактирование
        existingPlace?.let {
            binding.etName.setText(it.name)
            binding.etNewCategory.isEnabled = false
            binding.etDescription.setText(it.description)
            it.photoUri?.let { uriStr ->
                photoUri = Uri.parse(uriStr)
                binding.ivPhotoPreview.setImageURI(photoUri)
            }
        }

        // 6) Добавление новой категории
        binding.btnAddCategory.setOnClickListener {
            val name = binding.etNewCategory.text.toString().trim()
            if (name.isEmpty()) {
                binding.etNewCategory.error = "Введите название категории"
            } else {
                categoryVm.addCategory(name)
                binding.etNewCategory.text?.clear()
            }
        }

        // 7) Фото из галереи / камеры
        binding.btnChoosePhoto.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Добавить фото")
                .setItems(arrayOf("Из галереи", "С камеры")) { _, which ->
                    if (which == 0) pickFromGallery() else onCameraSelected()
                }
                .show()
        }

        // 8) Сохранить место
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            if (name.isEmpty()) {
                binding.etName.error = "Введите название"
                return@setOnClickListener
            }
            val sel = categoryAdapter.getSelectedIds()
            if (sel.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Выберите категорию",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val place = PlaceEntity(
                id = existingPlace?.id ?: 0L,
                name = name,
                category = "",
                description = desc,
                latitude = existingPlace?.latitude,
                longitude = existingPlace?.longitude,
                photoUri = photoUri?.toString()
            )
            if (existingPlace == null)
                placeVm.createPlaceWithCategories(place, sel)
            else
                placeVm.updatePlaceWithCategories(place.copy(id = existingPlace.id), sel)

            Toast.makeText(
                requireContext(),
                "Saved category IDs: $sel",
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }

        // 9) Отмена
        binding.btnCancel.setOnClickListener { dismiss() }

        // 10) Собираем и возвращаем диалог
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    // ==== Методы для работы с фото ====

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
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> takePhotoInternal()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Нужен доступ")
                    .setMessage("Разрешите доступ к камере")
                    .setPositiveButton("OK") { _, _ ->
                        requestCameraPermission.launch(Manifest.permission.CAMERA)
                    }
                    .show()
            else ->
                requestCameraPermission.launch(Manifest.permission.CAMERA)
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
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
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
                REQUEST_TAKE_PHOTO -> { }
            }
            binding.ivPhotoPreview.setImageURI(photoUri)
        } else super.onActivityResult(requestCode, resultCode, data)
    }
}
