package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.model.Track

interface HistoryRepository {
    fun getHistory(): List<Track>
    fun saveHistory(tracks: List<Track>)
    fun clearHistory()
}