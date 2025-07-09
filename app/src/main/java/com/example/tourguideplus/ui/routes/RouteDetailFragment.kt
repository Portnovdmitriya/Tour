package com.example.tourguideplus.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.FragmentRouteDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RouteDetailFragment : Fragment() {

    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: RouteViewModel
    private lateinit var placeAdapter: PlaceInRouteAdapter

    private var routeId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRouteDetailBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routeId = arguments?.getLong("routeId") ?: return

        vm = ViewModelProvider(
            this,
            RouteViewModelFactory(requireActivity().application as TourGuideApp)
        )[RouteViewModel::class.java]

        // Настраиваем RecyclerView
        placeAdapter = PlaceInRouteAdapter(emptyList()) { place ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить место")
                .setMessage("Убрать «${place.name}» из маршрута?")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Да") { _, _ ->
                    val current = vm.selected.value
                    if (current != null) {
                        val newIds = current.places.map { it.id }.toMutableList()
                        newIds.remove(place.id)
                        vm.updateRoute(current.route, newIds)
                    }
                }
                .show()
        }


        binding.rvPlacesInRoute.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlacesInRoute.adapter = placeAdapter
        binding.rvPlacesInRoute.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Наблюдаем выбранный маршрут
        vm.select(routeId)
        vm.selected.observe(viewLifecycleOwner) { rwp ->
            if (rwp != null) {
                binding.tvRouteName.text = rwp.route.name
                binding.tvRouteDesc.text = rwp.route.description ?: ""
                placeAdapter.update(rwp.places)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
