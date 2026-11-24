package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var playerState = STATE_DEFAULT

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        val player = mediaPlayer ?: MediaPlayer().also { mediaPlayer = it }
        player.reset()
        player.setDataSource(url)
        player.prepareAsync()
        player.setOnPreparedListener {
            playerState = STATE_PREPARED
            onPrepared()
        }
        player.setOnCompletionListener {
            playerState = STATE_PREPARED
            onCompletion()
        }
    }

    override fun startPlayer() {
        if (playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
            mediaPlayer?.start()
            playerState = STATE_PLAYING
        }
    }

    override fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer?.pause()
            playerState = STATE_PAUSED
        }
    }

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = STATE_DEFAULT
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return playerState == STATE_PLAYING
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}