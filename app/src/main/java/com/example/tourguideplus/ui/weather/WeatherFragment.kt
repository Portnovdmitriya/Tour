package com.example.tourguideplus.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.FragmentWeatherBinding
import com.example.tourguideplus.ui.settings.TemperatureSettingsViewModel
import com.example.tourguideplus.ui.settings.TemperatureSettingsViewModelFactory

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherVm: WeatherViewModel
    private lateinit var settingsVm: TemperatureSettingsViewModel

    // Будем хранить последнее значение в Цельсиях
    private var lastTempC: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentWeatherBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем WeatherViewModel
        weatherVm = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[WeatherViewModel::class.java]

        // Инициализируем TemperatureSettingsViewModel
        val app = requireActivity().application as TourGuideApp
        settingsVm = ViewModelProvider(
            this,
            TemperatureSettingsViewModelFactory(app)
        ).get(TemperatureSettingsViewModel::class.java)

        // Обработка кнопки «Показать погоду»
        binding.btnFetch.setOnClickListener {
            val city = binding.etCity.text.toString().trim()
            if (city.isEmpty()) {
                binding.etCity.error = "Введите город"
            } else {
                weatherVm.loadByCity(city)
            }
        }

        // Наблюдаем за ответом погоды
        weatherVm.weather.observe(viewLifecycleOwner) { st ->
            when (st) {
                is WeatherState.Loading -> {
                    // здесь можно показать ProgressBar
                }
                is WeatherState.Success -> {
                    val d = st.data
                    lastTempC = d.main.temp
                    binding.tvCity.text = d.city
                    binding.tvDesc.text = d.weather.firstOrNull()?.description ?: ""
                    binding.tvHumidity.text = "Влажность: ${d.main.humidity}%"
                    binding.tvWind.text = "Ветер: ${d.wind.speed} м/с"
                    val iconUrl = "https://openweathermap.org/img/wn/${d.weather[0].icon}@2x.png"
                    binding.ivIcon.load(iconUrl)
                    updateTempDisplay()  // сразу отобразим с учётом настройки
                }
                is WeatherState.Error -> {
                    Toast.makeText(requireContext(), st.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Наблюдаем за сменой настройки единицы измерения
        settingsVm.unit.observe(viewLifecycleOwner) {
            updateTempDisplay()
        }
    }

    /**
     * Конвертирует и показывает температуру в соответствии с lastTempC и выбранной настройкой.
     */
    private fun updateTempDisplay() {
        val c = lastTempC ?: return
        val unit = settingsVm.unit.value.orEmpty()
        val (value, suffix) = if (unit == "F") {
            val f = c * 9 / 5 + 32
            Pair(f, "°F")
        } else {
            Pair(c, "°C")
        }
        // Форматируем с одним знаком после запятой
        binding.tvTemp.text = String.format("%.1f %s", value, suffix)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}