package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class TrackResponse (
    val resultCount : Int,
    val results : List<TrackInf>
)

data class TrackInf(
    val trackName : String?,
    val artistName : String?,
    @SerializedName ("trackTimeMillis") val trackTimeMillis : Long,
    val artworkUrl100 : String?
)