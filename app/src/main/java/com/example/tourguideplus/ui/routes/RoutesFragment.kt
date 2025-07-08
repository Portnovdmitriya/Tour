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

        // Инициализируем адаптер для RouteWithPlaces
        adapter = RouteWithPlacesAdapter { routeWithPlaces ->
            // TODO: открыть детальный экран маршрута
            // findNavController().navigate(..., bundleOf("routeId" to routeWithPlaces.route.id))
        }
        binding.rvRoutes.adapter = adapter
        binding.rvRoutes.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                requireContext(),
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )

        // Подписываемся на новый LiveData
        vm.routesWithPlaces.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
