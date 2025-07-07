package com.example.tourguideplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tourguideplus.databinding.ActivityMainBinding
import com.example.tourguideplus.ui.main.AddEditPlaceDialogFragment
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding  // не File!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate с помощью viewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)  // <-- binding.root

        // Навигация
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Если нажали «Места» — всегда поп-бэк к корню списка
                R.id.placesFragment -> {
                    navController.popBackStack(R.id.placesFragment, false)
                    true
                }
                // Остальные пункты пусть работают по умолчанию через NavigationUI
                else -> NavigationUI.onNavDestinationSelected(menuItem, navController)
            }
        }

        // Пока что просто заглушка
        binding.fabAddPlace.setOnClickListener {
            AddEditPlaceDialogFragment().show(
                supportFragmentManager,
                "AddEditPlaceDialog"
            )
        }
    }
}
