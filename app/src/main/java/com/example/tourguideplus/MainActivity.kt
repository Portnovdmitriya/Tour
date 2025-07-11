package com.example.tourguideplus

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
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

        // Привязываем Toolbar
        setSupportActionBar(binding.toolbar)

        // Получаем NavController из NavHostFragment
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        // Привязываем BottomNavigationView
        binding.bottomNav.setupWithNavController(navController)

        // Показываем FAB только на вкладках Места и Маршруты
        navController.addOnDestinationChangedListener { _, dest, _ ->
            if (dest.id == R.id.placesFragment || dest.id == R.id.routesFragment)
                binding.fabAddPlace.show()
            else
                binding.fabAddPlace.hide()
        }

        // Обрабатываем клик FAB
        binding.fabAddPlace.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.placesFragment ->
                    AddEditPlaceDialogFragment().show(supportFragmentManager, "AddEditPlace")

                R.id.routesFragment ->
                    AddEditRouteDialogFragment().show(supportFragmentManager, "AddEditRoute")
            }
        }
    }

    // overflow-menu для «Категории»
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_categories -> {
            findNavController(R.id.nav_host_fragment)
                .navigate(R.id.categoriesFragment)
            true
        }

        R.id.menu_notes -> {
            findNavController(R.id.nav_host_fragment)
                .navigate(R.id.notesFragment)
            true
        }
        R.id.menu_profile -> {
            findNavController(R.id.nav_host_fragment)
                .navigate(R.id.profileFragment)
            true
        }
        R.id.menu_settings -> {
            findNavController(R.id.nav_host_fragment)
                .navigate(R.id.settingsFragment)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}

