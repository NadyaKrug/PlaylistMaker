package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.model.Track

class HistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : HistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history_key"
    }
}