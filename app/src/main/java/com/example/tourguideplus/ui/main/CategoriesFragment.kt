package com.example.tourguideplus.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.CategoryEntity
import com.example.tourguideplus.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?)
            = FragmentCategoriesBinding.inflate(inflater, c, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as TourGuideApp
        vm = ViewModelProvider(this, CategoryViewModelFactory(app))
            .get(CategoryViewModel::class.java)

        adapter = CategoryAdapter(
            onEdit   = { cat -> showEditDialog(cat) },
            onDelete = { cat -> vm.deleteCategory(cat) }
        )
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.rvCategories.adapter = adapter

        binding.fabAddCategory.setOnClickListener {
            showEditDialog(null)
        }

        vm.allCategories.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    private fun showEditDialog(existing: CategoryEntity?) {
        AddEditCategoryDialogFragment(existing)
            .show(parentFragmentManager, "AddEditCategory")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
