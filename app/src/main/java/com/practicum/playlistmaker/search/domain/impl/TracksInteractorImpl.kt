package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Track

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        repository.searchTracks(expression, object : TracksRepository.TracksConsumer {
            override fun consume(tracks: List<Track>?) {
                if (tracks == null) {
                    consumer.consume(null, ERROR_MARKER)
                } else {
                    consumer.consume(tracks, null)
                }
            }
        })
    }

    companion object {
        private const val ERROR_MARKER = "SERVER_ERROR"
    }
}