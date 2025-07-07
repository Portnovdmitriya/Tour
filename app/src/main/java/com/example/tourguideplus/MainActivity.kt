package com.example.tourguideplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.tourguideplus.databinding.ActivityMainBinding
import com.example.tourguideplus.ui.main.AddEditPlaceDialogFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)

        // Настраиваем BottomNavigation как раньше
        binding.bottomNav.setupWithNavController(navController)

        // Переопределяем поведение нажатия, чтобы "Места" всегда поп-бекали:
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.placesFragment -> {
                    navController.popBackStack(R.id.placesFragment, false)
                    true
                }
                else -> NavigationUI.onNavDestinationSelected(menuItem, navController)
            }
        }

        // Слушаем смену экрана — чтобы показывать/скрывать FAB
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.placesFragment) {
                binding.fabAddPlace.show()
            } else {
                binding.fabAddPlace.hide()
            }
        }

        // Обработчик FAB — нажатие открывает диалог создания места
        binding.fabAddPlace.setOnClickListener {
            AddEditPlaceDialogFragment().show(
                supportFragmentManager,
                "AddEditPlaceDialog"
            )
        }
    }
}

