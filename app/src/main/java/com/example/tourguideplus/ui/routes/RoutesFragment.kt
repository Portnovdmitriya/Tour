package com.example.tourguideplus.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteWithPlaces
import com.example.tourguideplus.databinding.FragmentRoutesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.recyclerview.widget.DividerItemDecoration

class RoutesFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: RouteViewModel
    private lateinit var adapter: RouteWithPlacesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRoutesBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(
            this,
            RouteViewModelFactory(requireActivity().application as TourGuideApp)
        )[RouteViewModel::class.java]

        // Инициализируем адаптер с колбэками на клик и на удаление
        adapter = RouteWithPlacesAdapter(
            onClick = { rwp ->
                // TODO: открыть детальный экран маршрута
            },
            onDelete = { rwp ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Удалить маршрут")
                    .setMessage("Вы уверены, что хотите удалить «${rwp.route.name}»?")
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Удалить") { _, _ ->
                        vm.deleteRoute(rwp.route)
                    }
                    .show()
            }
        )

        binding.rvRoutes.adapter = adapter
        binding.rvRoutes.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Подписываемся на список маршрутов и обновляем адаптер
        vm.routesWithPlaces.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
