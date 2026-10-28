package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.model.Track

interface TracksRepository {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(tracks: List<Track>?)
    }
}