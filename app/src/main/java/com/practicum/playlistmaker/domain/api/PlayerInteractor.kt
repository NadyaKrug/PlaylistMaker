package com.practicum.playlistmaker.domain.api

interface PlayerInteractor {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean

    interface PlayerStateCallback {
        fun onPrepared()
        fun onCompletion()
    }
}