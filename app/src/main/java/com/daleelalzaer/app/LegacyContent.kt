package com.daleelalzaer.app

import android.content.Context
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class QuoteEntry(
    val text: String,
    val source: String = "",
    val occasion: String = ""
)

@Serializable
data class BattleEventEntry(
    val day: Int,
    val title: String,
    val description: String,
    val source: String = ""
)

@Serializable
data class AhlulBaytEventEntry(
    val personName: String,
    val kind: String,
    val description: String,
    val source: String = ""
)

@Serializable
data class CityEntry(
    val name: String,
    val lat: Double,
    val lng: Double,
    val approxDistanceKm: Double = 0.0
)

@Serializable
data class ScholarEntry(
    val id: String,
    val name: String,
    val title: String,
    val officialSite: String = "",
    val istiftaUrl: String = "",
    val isLiving: Boolean = false,
    val hasRss: Boolean = false
)

@Serializable
data class ZiaratEntry(
    val title: String,
    val subtitle: String,
    val content: String
)

typealias DuaEntry = ZiaratEntry

@Serializable
data class SacredPlaceEntry(
    val name: String,
    val subtitle: String,
    val lat: Double,
    val lng: Double
)

@Serializable
data class HijriMonth(
    val name: String,
    val number: Int,
    val hijriYear: Int,
    val startDate: String,
    val days: Int
)

@Serializable
data class HijriCalendarData(
    val hijriMonths: List<HijriMonth>,
    val lastUpdated: String
)

object LegacyAssets {
    private val json = Json { ignoreUnknownKeys = true }

    fun <T> loadList(
        context: Context,
        name: String,
        serializer: KSerializer<T>
    ): List<T> {
        return runCatching {
            context.assets.open(name).bufferedReader().use { reader ->
                json.decodeFromString(
                    ListSerializer(serializer),
                    reader.readText()
                )
            }
        }.getOrDefault(emptyList())
    }
}
