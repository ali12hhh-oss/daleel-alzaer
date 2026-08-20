package com.daleelalzaer.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class NativeRoutePoint(val lat: Double, val lng: Double)

@Serializable
data class NativeRouteOption(
    val distanceKm: Double,
    val durationMin: Double,
    val points: List<NativeRoutePoint>,
    val name: String,
    val instructions: List<String> = emptyList()
)

object RouteEngine {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun calculate(
        fromLat: Double,
        fromLng: Double,
        toLat: Double,
        toLng: Double
    ): List<NativeRouteOption> = withContext(Dispatchers.IO) {
        val url = URL(
            "https://router.project-osrm.org/route/v1/driving/" +
                "$fromLng,$fromLat;$toLng,$toLat" +
                "?overview=full&geometries=geojson&alternatives=true&steps=true"
        )

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 35_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "daleel-alzaer-native/1.0")
        }

        try {
            val code = connection.responseCode
            if (code !in 200..299) throw IllegalStateException("تعذر الاتصال بمحرك حساب الطرق")
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            parse(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun parse(body: String): List<NativeRouteOption> {
        val root = json.parseToJsonElement(body).jsonObject
        if (root["code"]?.toString()?.trim('"') != "Ok") {
            throw IllegalStateException("لم يتم العثور على طريق مناسب لهذه الوجهة")
        }

        val routes = root["routes"]?.jsonArray ?: return emptyList()
        return routes.mapIndexedNotNull { index, routeElement ->
            val route = routeElement.jsonObject
            val distance = route["distance"]?.toString()?.toDoubleOrNull() ?: return@mapIndexedNotNull null
            val duration = route["duration"]?.toString()?.toDoubleOrNull() ?: return@mapIndexedNotNull null
            val coordinates = route["geometry"]?.jsonObject?.get("coordinates")?.jsonArray ?: return@mapIndexedNotNull null
            val points = coordinates.mapNotNull { pair ->
                val a = pair.jsonArray
                val lng = a.getOrNull(0)?.toString()?.toDoubleOrNull()
                val lat = a.getOrNull(1)?.toString()?.toDoubleOrNull()
                if (lat != null && lng != null) NativeRoutePoint(lat, lng) else null
            }
            if (points.size < 2) return@mapIndexedNotNull null

            val instructions = buildList {
                route["legs"]?.jsonArray?.forEach { legElement ->
                    legElement.jsonObject["steps"]?.jsonArray?.forEach { stepElement ->
                        val step = stepElement.jsonObject
                        val maneuver = step["maneuver"]?.jsonObject
                        val type = maneuver?.get("type")?.toString()?.trim('"')
                        val modifier = maneuver?.get("modifier")?.toString()?.trim('"')
                        val road = step["name"]?.toString()?.trim('"').orEmpty()
                        instruction(type, modifier, road)?.let(::add)
                    }
                }
            }.take(12)

            NativeRouteOption(
                distanceKm = distance / 1000.0,
                durationMin = duration / 60.0,
                points = points,
                name = if (index == 0) "المسار الأقرب" else "مسار بديل $index",
                instructions = instructions
            )
        }.sortedBy { it.distanceKm }
            .mapIndexed { index, route -> route.copy(name = if (index == 0) "المسار الأقرب" else "مسار بديل $index") }
    }

    private fun instruction(type: String?, modifier: String?, road: String): String? {
        val suffix = if (road.isBlank()) "" else " على $road"
        return when (type) {
            "depart" -> "انطلق$suffix"
            "arrive" -> "لقد وصلت إلى الوجهة"
            "turn" -> "انعطف ${when (modifier) { "left" -> "يسارًا"; "right" -> "يمينًا"; else -> "مباشرة" }}$suffix"
            "new name", "continue" -> "تابع مباشرة$suffix"
            "merge" -> "اندمج في الطريق$suffix"
            "fork" -> "خذ الفرع المناسب$suffix"
            "roundabout" -> "ادخل الدوار$suffix"
            else -> if (suffix.isBlank()) "تابع على الطريق" else "تابع$suffix"
        }
    }
}
