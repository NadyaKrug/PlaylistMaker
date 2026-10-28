package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.model.Track

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        repository.searchTracks(expression, object : TracksRepository.TracksConsumer {
            override fun consume(tracks: List<Track>?) {
                consumer.consume(tracks, null)
            }
        })
    }
}