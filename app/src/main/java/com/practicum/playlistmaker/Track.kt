package com.practicum.playlistmaker

import java.io.Serializable

data class Track (
    val trackName : String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val trackId : Long,
    val collectionName : String?,
    val releaseDate : String?,
    val primaryGenreName : String?,
    val country : String

) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun getRealeasedDate() : String? {
        return if(!releaseDate.isNullOrEmpty() && releaseDate.length >=4){
            releaseDate.substring(0,4)
        } else {
            null
        }
    }
}