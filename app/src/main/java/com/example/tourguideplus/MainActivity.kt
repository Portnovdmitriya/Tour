package com.example.tourguideplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tourguideplus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding  // не File!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate с помощью viewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)  // <-- binding.root

        // Навигация
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setupWithNavController(navController)

        // Пока что просто заглушка
        binding.fabAddPlace.setOnClickListener {
            // TODO: покажем AddPlaceDialogFragment или другой UI
        }
    }
}
