package com.example.tourguideplus.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.tourguideplus.databinding.FragmentWeatherBinding
import com.google.android.gms.location.LocationServices

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: WeatherViewModel

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) fetchLocation()
            else Toast.makeText(requireContext(),
                "Нужны разрешения на локацию", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentWeatherBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application))
            .get(WeatherViewModel::class.java)

        // Наблюдаем состояние и отображаем
        vm.weather.observe(viewLifecycleOwner) { st ->
            when (st) {
                is WeatherState.Loading -> {
                    // Можно показать прогресс
                }
                is WeatherState.Success -> {
                    val d = st.data
                    binding.tvCity.text = d.city
                    binding.tvTemp.text = "${d.main.temp} °C"
                    binding.tvDesc.text = d.weather.firstOrNull()?.description ?: ""
                    binding.tvHumidity.text = "Влажность: ${d.main.humidity}%"
                    binding.tvWind.text = "Ветер: ${d.wind.speed} м/с"
                    // Загрузка иконки
                    val iconUrl = "https://openweathermap.org/img/wn/${d.weather[0].icon}@2x.png"
                    binding.ivIcon.load(iconUrl)
                }
                is WeatherState.Error -> {
                    Toast.makeText(requireContext(), st.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Запрашиваем разрешение
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        } else {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        val client = LocationServices.getFusedLocationProviderClient(requireContext())
        client.lastLocation.addOnSuccessListener { loc: Location? ->
            if (loc != null) vm.load(loc.latitude, loc.longitude)
            else Toast.makeText(requireContext(),
                "Не удалось получить локацию", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
