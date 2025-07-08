package com.example.tourguideplus.ui.routes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.FragmentRoutesBinding
import androidx.core.os.bundleOf

class RoutesFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: RouteViewModel
    private lateinit var adapter: RouteAdapter

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, b: Bundle?) =
        FragmentRoutesBinding.inflate(inflater, c, false).also { _binding = it }.root

    override fun onViewCreated(v: View, saved: Bundle?) {
        super.onViewCreated(v, saved)

        vm = ViewModelProvider(
            this,
            RouteViewModelFactory(requireActivity().application as TourGuideApp)
        )[RouteViewModel::class.java]

        adapter = RouteAdapter { route ->
            // TODO: можно сделать детальный экран маршрута
            // findNavController().navigate(R.id.action_to_routeDetail, bundleOf("routeId" to route.id))
        }
        binding.rvRoutes.adapter = adapter
        binding.rvRoutes.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        vm.routes.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // Окошко добавления — через FAB, который показываем только на этом фрагменте
        // Или добавьте в toolbar пункт меню «+»
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
