package com.example.tourguideplus.deeplink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tourguideplus.MainActivity
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.share.RouteShareCodec
import com.example.tourguideplus.share.toPlaceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteImportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data == null) {
            Toast.makeText(this, "Ссылка без данных маршрута", Toast.LENGTH_LONG).show()
            finishAndGoHome()
            return
        }
        importFromUri(data)
    }

    private fun importFromUri(uri: Uri) {
        val payload = uri.getQueryParameter("payload")
        if (payload.isNullOrEmpty()) {
            Toast.makeText(this, "Не найден payload", Toast.LENGTH_LONG).show()
            finishAndGoHome()
            return
        }

        lifecycleScope.launch {
            val ok = withContext(Dispatchers.IO) {
                try {
                    val dto = RouteShareCodec.decode(payload) ?: return@withContext false

                    val app = application as TourGuideApp
                    val placeRepo = app.placeRepository
                    val routeRepo = app.routeRepository

                    // Создаём/находим места (MVP: по имени)
                    val placeIds = dto.places.map { sp ->
                        val existing = try { placeRepo.findByName(sp.name) } catch (_: Exception) { null }
                        existing?.id ?: placeRepo.insertReturnId(sp.toPlaceEntity())
                    }

                    val routeName = dto.name.ifBlank { "Маршрут" } + " (импорт)"
                    val route = RouteEntity(name = routeName, description = dto.description)
                    routeRepo.createRouteWithPlaces(route, placeIds)

                    true
                } catch (_: Exception) {
                    false
                }
            }

            Toast.makeText(
                this@RouteImportActivity,
                if (ok) "Маршрут импортирован" else "Не удалось импортировать маршрут",
                Toast.LENGTH_LONG
            ).show()
            finishAndGoHome()
        }
    }

    private fun finishAndGoHome() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
        finish()
    }
}
