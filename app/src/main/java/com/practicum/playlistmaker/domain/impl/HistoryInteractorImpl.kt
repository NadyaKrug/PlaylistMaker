package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.model.Track

class HistoryInteractorImpl(private val historyRepository: HistoryRepository) : HistoryInteractor {

    override fun getHistory(): List<Track> {
        return historyRepository.getHistory()
    }

    override fun addTrack(track: Track) {
        val history = historyRepository.getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        historyRepository.saveHistory(history)
    }

    override fun clearHistory() {
        historyRepository.clearHistory()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}