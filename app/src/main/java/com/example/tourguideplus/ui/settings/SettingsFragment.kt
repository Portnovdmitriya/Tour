package com.example.tourguideplus.ui.settings

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!
    private lateinit var vm: TemperatureSettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSettingsBinding.inflate(inflater, container, false)
            .also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val app = requireActivity().application as TourGuideApp
        vm = ViewModelProvider(this, TemperatureSettingsViewModelFactory(app))
            .get(TemperatureSettingsViewModel::class.java)

        // При старте — установить радиокнопку
        vm.unit.observe(viewLifecycleOwner) { u ->
            if (u == "F") b.rbFahrenheit.isChecked = true
            else          b.rbCelsius.isChecked    = true
        }

        // Обработчики переключения
        b.rgTempUnit.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rbCelsius    -> vm.setUnit("C")
                R.id.rbFahrenheit -> vm.setUnit("F")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
