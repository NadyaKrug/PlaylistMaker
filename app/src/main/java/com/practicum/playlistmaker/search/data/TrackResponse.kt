package com.practicum.playlistmaker.search.data

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.search.domain.model.Track

data class TrackResponse (
    val resultCount : Int,
    val results : List<Track>
)

data class TrackInf(
    val trackName : String?,
    val artistName : String?,
    @SerializedName ("trackTimeMillis") val trackTimeMillis : Long,
    val artworkUrl100 : String?
)