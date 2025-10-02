package com.example.tourguideplus

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.tourguideplus.databinding.ActivityMainBinding
import com.example.tourguideplus.ui.main.AddEditPlaceDialogFragment
import com.example.tourguideplus.ui.routes.AddEditRouteDialogFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Привязываем Toolbar
        setSupportActionBar(binding.toolbar)

        // Получаем NavController из NavHostFragment
        val app = application as TourGuideApp
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        // Привязываем BottomNavigationView
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, dest, _ ->
            val isAuth = dest.id == R.id.authFragment
            binding.bottomNav.visibility = if (isAuth) View.GONE else View.VISIBLE
            if (isAuth) binding.fabAddPlace.hide()
        }
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

        app.settingRepository.getSetting("auth_user").observe(this) { st ->
            val loggedIn = !st?.value.isNullOrEmpty()
            if (!loggedIn && navController.currentDestination?.id != R.id.authFragment) {
                navController.navigate(R.id.authFragment)
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
        // Новый пункт «Справка» в overflow:
        R.id.menu_help -> {
            findNavController(R.id.nav_host_fragment)
                .navigate(R.id.helpFragment)
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

