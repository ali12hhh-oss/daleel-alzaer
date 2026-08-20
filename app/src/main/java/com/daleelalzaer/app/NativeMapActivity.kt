package com.daleelalzaer.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.Locale

class NativeMapActivity : ComponentActivity(), CoroutineScope by MainScope() {
    private lateinit var map: MapView
    private lateinit var status: TextView
    private var offline = false
    private var destinationLat = LocationSupport.HUSSAIN_SHRINE_LAT
    private var destinationLng = LocationSupport.HUSSAIN_SHRINE_LNG
    private var destinationName = "مرقد الإمام الحسين (ع)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        offline = intent.getBooleanExtra("offline", false)
        destinationLat = intent.getDoubleExtra("lat", destinationLat)
        destinationLng = intent.getDoubleExtra("lng", destinationLng)
        destinationName = intent.getStringExtra("name") ?: destinationName

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        status = TextView(this).apply {
            textSize = 15f
            setPadding(20, 18, 20, 18)
            text = if (offline) "الخريطة أوفلاين — $destinationName" else "خرائط أونلاين — $destinationName"
        }
        root.addView(status, LinearLayout.LayoutParams(-1, -2))

        map = MapView(this).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setUseDataConnection(!offline)
            controller.setZoom(10.0)
            controller.setCenter(GeoPoint(destinationLat, destinationLng))
        }
        root.addView(map, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)

        addDestinationMarker()
        if (hasLocationPermission()) calculateRoute()
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun calculateRoute() {
        launch {
            val location = runCatching {
                LocationServices.getFusedLocationProviderClient(this@NativeMapActivity).lastLocation.await()
            }.getOrNull() ?: return@launch

            if (offline) {
                val saved = withContext(Dispatchers.IO) {
                    OfflineMapService.findRoutes(
                        this@NativeMapActivity,
                        location.latitude,
                        location.longitude,
                        destinationLat,
                        destinationLng,
                        destinationName
                    )
                }
                if (saved.isEmpty()) {
                    status.text = "لا يوجد مسار محفوظ لهذه الوجهة. افتح الأونلاين أولاً واحسب المسار ثم احفظ الخريطة."
                    drawLine(listOf(GeoPoint(location.latitude, location.longitude), GeoPoint(destinationLat, destinationLng)))
                    return@launch
                }
                drawLine(saved.first().points.map { GeoPoint(it.lat, it.lng) })
                status.text = "أوفلاين • ${format(saved.first().distanceKm)} كم • ${formatMinutes(saved.first().durationMin)}"
                return@launch
            }

            status.text = "جاري حساب المسار..."
            runCatching {
                RouteEngine.calculate(location.latitude, location.longitude, destinationLat, destinationLng)
            }.onSuccess { routes ->
                val route = routes.firstOrNull()
                if (route == null) {
                    status.text = "لم يعثر محرك الخرائط على مسار."
                    return@onSuccess
                }
                val saved = SavedNativeRouteSet(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    destinationLatitude = destinationLat,
                    destinationLongitude = destinationLng,
                    destinationName = destinationName,
                    routes = routes.map { SavedNativeRoute(it.distanceKm, it.durationMin, it.points) },
                    savedAt = System.currentTimeMillis()
                )
                withContext(Dispatchers.IO) {
                    OfflineMapService.saveRoutes(this@NativeMapActivity, saved)
                }
                drawLine(route.points.map { GeoPoint(it.lat, it.lng) })
                status.text = "أونلاين • ${format(route.distanceKm)} كم • ${formatMinutes(route.durationMin)}"
            }.onFailure {
                status.text = "تعذر حساب المسار: ${it.message ?: "خطأ غير معروف"}"
            }
        }
    }

    private fun addDestinationMarker() {
        val marker = Marker(map).apply {
            position = GeoPoint(destinationLat, destinationLng)
            title = destinationName
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        map.overlays.add(marker)
        map.invalidate()
    }

    private fun drawLine(points: List<GeoPoint>) {
        if (points.size < 2) return
        runOnUiThread {
            map.overlays.removeAll { it is Polyline }
            val line = Polyline(map).apply {
                setPoints(points)
                width = 8f
            }
            map.overlays.add(line)
            map.controller.setCenter(points[points.size / 2])
            map.invalidate()
        }
    }

    private fun format(value: Double) = String.format(Locale.US, "%.1f", value)
    private fun formatMinutes(value: Double): String {
        val total = value.toInt().coerceAtLeast(0)
        return if (total < 60) "$total دقيقة" else "${total / 60} س ${total % 60} د"
    }

    override fun onResume() { super.onResume(); map.onResume() }
    override fun onPause() { map.onPause(); super.onPause() }
}
