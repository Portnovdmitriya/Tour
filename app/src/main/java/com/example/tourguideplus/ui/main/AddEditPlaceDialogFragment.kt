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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val requestCameraPermission =
        registerForActivityResult(RequestPermission()) { granted ->
            if (granted) takePhotoInternal()
            else Toast.makeText(
                requireContext(),
                "Без доступа к камере сделать фото нельзя",
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEditPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 1) ViewModels
        val app = requireActivity().application as TourGuideApp
        placeVm = ViewModelProvider(this, PlaceViewModelFactory(app))
            .get(PlaceViewModel::class.java)
        categoryVm = ViewModelProvider(this, CategoryViewModelFactory(app))
            .get(CategoryViewModel::class.java)

        // 2) RecyclerView + Adapter
        // 4) Настраиваем RecyclerView для категорий
        categoryAdapter = SelectableCategoryAdapter { _, _ -> /* колбек нам не нужен */ }
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = categoryAdapter

// 5) Подписываемся на общий список категорий
        categoryVm.allCategories.observe(viewLifecycleOwner) { cats ->
            // вот теперь у нас есть submitList
            categoryAdapter.submitList(cats)

            // если редактируем — подгружаем уже сохранённые категории этого места
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

        // 4) Заполняем поля, если редактируем
        existingPlace?.let {
            binding.etName.setText(it.name)
            binding.etNewCategory.isEnabled = false
            binding.etDescription.setText(it.description)
            it.photoUri?.let { uriStr ->
                photoUri = Uri.parse(uriStr)
                binding.ivPhotoPreview.setImageURI(photoUri)
            }
        }

        // 5) Добавление новой категории
        binding.btnAddCategory.setOnClickListener {
            val newName = binding.etNewCategory.text.toString().trim()
            if (newName.isEmpty()) {
                binding.etNewCategory.error = "Введите название категории"
            } else {
                categoryVm.addCategory(newName)
                binding.etNewCategory.text?.clear()
            }
        }

        // 6) Фото из галереи / камеры
        binding.btnChoosePhoto.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Добавить фото")
                .setItems(arrayOf("Из галереи", "С камеры")) { _, which ->
                    if (which == 0) pickFromGallery() else onCameraSelected()
                }
                .show()
        }

        // 7) Сохранить место
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            if (name.isEmpty()) {
                binding.etName.error = "Введите название"
                return@setOnClickListener
            }
            val selIds = categoryAdapter.getSelectedIds()
            if (selIds.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Выберите хотя бы одну категорию",
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
            if (existingPlace == null) {
                placeVm.createPlaceWithCategories(place, selIds)
            } else {
                placeVm.updatePlaceWithCategories(place.copy(id = existingPlace.id), selIds)
            }
            Toast.makeText(requireContext(),
                "Saved category IDs: $selIds",
                Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // 8) Отмена
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // просто оборачиваем уже инфлейченный view
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    // ==== Методы для фото ====

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
