package com.example.tourguideplus.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.tourguideplus.databinding.FragmentWeatherBinding

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentWeatherBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[WeatherViewModel::class.java]

        // Обработка нажатия
        binding.btnFetch.setOnClickListener {
            val city = binding.etCity.text.toString().trim()
            if (city.isEmpty()) {
                binding.etCity.error = "Введите город"
            } else {
                vm.loadByCity(city)
            }
        }

        // Наблюдаем UI
        vm.weather.observe(viewLifecycleOwner) { st ->
            when (st) {
                is WeatherState.Loading -> {
                    // Можно показывать прогресс
                }
                is WeatherState.Success -> {
                    val d = st.data
                    binding.tvCity.text = d.city
                    binding.tvTemp.text = "${d.main.temp} °C"
                    binding.tvDesc.text = d.weather.firstOrNull()?.description ?: ""
                    binding.tvHumidity.text = "Влажность: ${d.main.humidity}%"
                    binding.tvWind.text = "Ветер: ${d.wind.speed} м/с"
                    val iconUrl = "https://openweathermap.org/img/wn/${d.weather[0].icon}@2x.png"
                    binding.ivIcon.load(iconUrl)
                }
                is WeatherState.Error -> {
                    Toast.makeText(requireContext(), st.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
