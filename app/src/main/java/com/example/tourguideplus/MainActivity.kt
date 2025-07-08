package com.example.tourguideplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.tourguideplus.databinding.ActivityMainBinding
import com.example.tourguideplus.ui.main.AddEditPlaceDialogFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.tourguideplus.ui.routes.AddEditRouteDialogFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)

        // Привязываем меню к NavController
        binding.bottomNav.setupWithNavController(navController)


        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.placesFragment -> {
                    navController.popBackStack(R.id.placesFragment, false)
                    true
                }
                else -> NavigationUI.onNavDestinationSelected(menuItem, navController)
            }
        }

        // FAB только на экранах Places и Routes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.placesFragment,
                R.id.navigation_routes -> binding.fabAddPlace.show()
                else -> binding.fabAddPlace.hide()
            }
        }


        binding.fabAddPlace.setOnClickListener {
            val dest = navController.currentDestination?.id
            when (dest) {
                R.id.placesFragment -> AddEditPlaceDialogFragment()
                    .show(supportFragmentManager, "AddEditPlace")
                R.id.navigation_routes -> AddEditRouteDialogFragment()
                    .show(supportFragmentManager, "AddEditRoute")
            }
        }
    }
}


