package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.model.Track

interface HistoryRepository {
    fun getHistory(): List<Track>
    fun saveHistory(tracks: List<Track>)
    fun clearHistory()
}