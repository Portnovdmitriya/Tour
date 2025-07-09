package com.example.tourguideplus

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tourguideplus.databinding.ActivityMainBinding
import com.example.tourguideplus.ui.main.AddEditPlaceDialogFragment
import com.example.tourguideplus.ui.routes.AddEditRouteDialogFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment)

        // 1) Привязываем BottomNavigation к NavController
        binding.bottomNav.setupWithNavController(navController)

        // 2) Показываем FAB только на вкладках "Места" и "Маршруты"
        navController.addOnDestinationChangedListener { _, dest, _ ->
            if (dest.id == R.id.placesFragment ||
                dest.id == R.id.navigation_routes
            ) {
                binding.fabAddPlace.show()
            } else {
                binding.fabAddPlace.hide()
            }
        }

        // 3) Обработка нажатия FAB: добавляем место или маршрут
        binding.fabAddPlace.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.placesFragment ->
                    AddEditPlaceDialogFragment()
                        .show(supportFragmentManager, "AddEditPlace")
                R.id.navigation_routes ->
                    AddEditRouteDialogFragment()
                        .show(supportFragmentManager, "AddEditRoute")
            }
        }
    }

    // --- overflow-меню для "Категорий" ---
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_categories -> {
                // Навигация к CategoriesFragment
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.categoriesFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
