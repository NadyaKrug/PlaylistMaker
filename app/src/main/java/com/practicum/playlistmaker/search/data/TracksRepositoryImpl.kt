package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TracksRepositoryImpl(private val trackApiService: TrackAPI) : TracksRepository {

    override fun searchTracks(expression: String, consumer: TracksRepository.TracksConsumer) {
        trackApiService.search(expression).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.code() == 200) {
                    val results = response.body()?.results?.map {
                        Track(
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = it.trackTimeMillis,
                            artworkUrl100 = it.artworkUrl100,
                            trackId = it.trackId,
                            collectionName = it.collectionName,
                            releaseDate = it.releaseDate,
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl
                        )
                    }
                    consumer.consume(results ?: emptyList())
                } else {
                    consumer.consume(null)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                consumer.consume(null)
            }
        })
    }
}