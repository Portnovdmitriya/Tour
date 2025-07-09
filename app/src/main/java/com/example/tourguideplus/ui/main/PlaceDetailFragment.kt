package com.example.tourguideplus.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope            // <- для корутин
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceWithCategories  // <- новая модель
import com.example.tourguideplus.databinding.FragmentPlaceDetailBinding
import kotlinx.coroutines.launch                // <- для launch
import android.content.Intent
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaceDetailFragment : Fragment() {
    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!

    // заменяем ViewModel одной только сущности на две:
    private lateinit var placeVm: PlaceViewModel
    private lateinit var catVm: CategoryViewModel    // <- для загрузки связей

    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as TourGuideApp
        val placeVm = ViewModelProvider(this, PlaceViewModelFactory(app))
            .get(PlaceViewModel::class.java)
        val catVm = ViewModelProvider(this, CategoryViewModelFactory(app))
            .get(CategoryViewModel::class.java)

        val placeId = arguments?.getLong("placeId") ?: return

        // Загружаем полностью place + его категории
        lifecycleScope.launch {
            val pwc = catVm.getPlaceWithCategories(placeId)
            if (pwc != null) {
                // Отладочный тост
                Toast.makeText(requireContext(),
                    "Detail categories: ${pwc.categories.map { it.name }}",
                    Toast.LENGTH_LONG).show()

                binding.tvName.text = pwc.place.name
                binding.tvCategory.text = pwc.categories.joinToString(", ") { it.name }
                binding.tvDescription.text = pwc.place.description
                pwc.place.photoUri?.let { uri ->
                    binding.ivPhoto.setImageURI(Uri.parse(uri))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
