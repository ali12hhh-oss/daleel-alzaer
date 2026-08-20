package com.daleelalzaer.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persistent storage for the full 30-day Qada prayer grid.
 * This mirrors the legacy screen's local record model without changing
 * the currently verified navigation/build path yet.
 */
object QadaPrayerService {
    private const val PREFS = "qada_prayer_v2"
    private const val KEY_DATA = "data"
    val prayers = listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")
    const val DAYS = 30

    data class State(
        val personName: String = "",
        val grid: List<List<Boolean>> = emptyGrid()
    )

    fun emptyGrid(): List<List<Boolean>> = List(DAYS) { List(prayers.size) { false } }

    fun load(context: Context): State {
        return runCatching {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val raw = prefs.getString(KEY_DATA, null) ?: return State()
            val root = JSONObject(raw)
            val rows = root.optJSONArray("days")
            val grid = if (rows == null) emptyGrid() else List(DAYS) { d ->
                List(prayers.size) { i ->
                    rows.optJSONObject(d)?.optBoolean(prayers[i], false) ?: false
                }
            }
            State(root.optString("personName", ""), grid)
        }.getOrDefault(State())
    }

    fun save(context: Context, state: State) {
        val root = JSONObject()
        root.put("personName", state.personName)
        val rows = JSONArray()
        state.grid.forEachIndexed { day, row ->
            val item = JSONObject()
            item.put("day", day + 1)
            prayers.forEachIndexed { i, prayer -> item.put(prayer, row.getOrElse(i) { false }) }
            rows.put(item)
        }
        root.put("days", rows)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_DATA, root.toString()).apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun backupJson(state: State): String {
        val root = JSONObject()
        root.put("app", "دليل الزائر")
        root.put("backupType", "qada_prayer")
        root.put("personName", state.personName)
        val rows = JSONArray()
        state.grid.forEachIndexed { day, row ->
            val item = JSONObject()
            item.put("day", day + 1)
            prayers.forEachIndexed { i, prayer -> item.put(prayer, row.getOrElse(i) { false }) }
            rows.put(item)
        }
        root.put("days", rows)
        return root.toString(2)
    }

    fun restoreJson(raw: String): State {
        val root = JSONObject(raw)
        val rows = root.optJSONArray("days") ?: throw IllegalArgumentException("ملف النسخة الاحتياطية غير صالح")
        val grid = List(DAYS) { d ->
            List(prayers.size) { i -> rows.optJSONObject(d)?.optBoolean(prayers[i], false) ?: false }
        }
        return State(root.optString("personName", ""), grid)
    }
}
