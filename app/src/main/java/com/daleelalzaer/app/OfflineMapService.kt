package com.daleelalzaer.app

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

@Serializable
data class SavedNativeRoute(
    val distanceKm: Double,
    val durationMin: Double,
    val points: List<NativeRoutePoint>
)

@Serializable
data class SavedNativeRouteSet(
    val latitude: Double,
    val longitude: Double,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val destinationName: String,
    val routes: List<SavedNativeRoute>,
    val savedAt: Long
)

object OfflineMapService {
    const val tileUrlTemplate = "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
    private const val fallbackTileUrlTemplate = "https://a.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}.png"
    private const val routesFileName = "saved_route_options_v2.json"

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private fun root(context: Context): File = File(context.filesDir, "map_tiles")
    private fun routeFile(context: Context): File = File(context.filesDir, routesFileName)

    fun latLngToTile(lat: Double, lng: Double, zoom: Int): Pair<Int, Int> {
        val safeLat = lat.coerceIn(-85.05112878, 85.05112878)
        val n = 1 shl zoom
        val x = floor((lng + 180.0) / 360.0 * n).toInt().coerceIn(0, n - 1)
        val latRad = safeLat * PI / 180.0
        val y = floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * n)
            .toInt().coerceIn(0, n - 1)
        return x to y
    }

    fun cachedTile(context: Context, z: Int, x: Int, y: Int): File? {
        val file = File(root(context), "$z/$x/$y.png")
        return file.takeIf { it.exists() && it.length() > 0L }
    }

    suspend fun downloadRegion(
        context: Context,
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double,
        minZoom: Int,
        maxZoom: Int,
        onProgress: (downloaded: Int, total: Int, failed: Int) -> Unit = { _, _, _ -> }
    ) {
        val tiles = mutableListOf<Triple<Int, Int, Int>>()
        for (z in minZoom..maxZoom) {
            val topLeft = latLngToTile(maxLat, minLng, z)
            val bottomRight = latLngToTile(minLat, maxLng, z)
            for (x in topLeft.first..bottomRight.first) {
                for (y in topLeft.second..bottomRight.second) {
                    tiles += Triple(z, x, y)
                }
            }
        }

        var downloaded = 0
        var failed = 0
        onProgress(0, tiles.size, 0)

        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            for (tile in tiles) {
                val (z, x, y) = tile
                val file = File(root(context), "$z/$x/$y.png")

                if (file.exists() && file.length() > 0L) {
                    downloaded++
                    onProgress(downloaded, tiles.size, failed)
                    continue
                }

                try {
                    val bytes = request(tileUrlTemplate, z, x, y)
                        ?: request(fallbackTileUrlTemplate, z, x, y)

                    if (bytes != null && bytes.isNotEmpty()) {
                        file.parentFile?.mkdirs()
                        file.writeBytes(bytes)
                        downloaded++
                    } else {
                        failed++
                    }
                } catch (_: Throwable) {
                    failed++
                }

                onProgress(downloaded, tiles.size, failed)
            }
        }
    }

    private fun request(template: String, z: Int, x: Int, y: Int): ByteArray? {
        val url = URL(
            template.replace("{z}", "$z")
                .replace("{x}", "$x")
                .replace("{y}", "$y")
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 12_000
            setRequestProperty("User-Agent", "daleel-alzaer-native/1.0")
        }
        return try {
            if (connection.responseCode != 200) {
                null
            } else {
                connection.inputStream.use { it.readBytes() }
            }
        } finally {
            connection.disconnect()
        }
    }

    fun cacheSizeMb(context: Context): Double {
        fun size(file: File): Long =
            if (file.isFile) file.length() else file.listFiles()?.sumOf(::size) ?: 0L
        return size(root(context)) / 1024.0 / 1024.0
    }

    fun clearCache(context: Context) {
        root(context).deleteRecursively()
        root(context).mkdirs()
    }

    fun saveRoutes(context: Context, set: SavedNativeRouteSet) {
        val existing = loadRouteSets(context).toMutableList()
        val key = routeKey(set)
        existing.removeAll { routeKey(it) == key }
        existing += set
        routeFile(context).writeText(
            json.encodeToString(
                ListSerializer(SavedNativeRouteSet.serializer()),
                existing
            )
        )
    }

    fun loadRouteSets(context: Context): List<SavedNativeRouteSet> = runCatching {
        val file = routeFile(context)
        if (!file.exists()) {
            emptyList()
        } else {
            json.decodeFromString(
                ListSerializer(SavedNativeRouteSet.serializer()),
                file.readText()
            )
        }
    }.getOrDefault(emptyList())

    fun findRoutes(
        context: Context,
        fromLat: Double?,
        fromLng: Double?,
        destinationLat: Double,
        destinationLng: Double,
        destinationName: String
    ): List<SavedNativeRoute> {
        val candidates = loadRouteSets(context).filter {
            it.destinationName == destinationName &&
                almost(it.destinationLatitude, destinationLat) &&
                almost(it.destinationLongitude, destinationLng)
        }
        if (fromLat == null || fromLng == null) {
            return candidates.firstOrNull()?.routes ?: emptyList()
        }
        return candidates.minByOrNull {
            distance(it.latitude, it.longitude, fromLat, fromLng)
        }?.routes ?: emptyList()
    }

    private fun routeKey(s: SavedNativeRouteSet) =
        "${s.latitude},${s.longitude}|${s.destinationLatitude},${s.destinationLongitude}|${s.destinationName}"

    private fun almost(a: Double, b: Double) = kotlin.math.abs(a - b) < 0.0005

    private fun distance(aLat: Double, aLng: Double, bLat: Double, bLng: Double): Double =
        LocationSupport.distanceKm(aLat, aLng, bLat, bLng)
}
