package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.ui.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val url: String
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState

    private val handler = Handler(Looper.getMainLooper())
    private var playerStateEnum = STATE_DEFAULT

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (playerStateEnum == STATE_PLAYING) {
                val currentPosition = playerInteractor.getCurrentPosition()
                _playerState.value = PlayerState.Progress(
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                )
                handler.postDelayed(this, REFRESH_TIMER_DELAY)
            }
        }
    }

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        if (url.isEmpty()) return
        playerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                _playerState.value = PlayerState.Prepared
                playerStateEnum = STATE_PREPARED
            },
            onCompletion = {
                handler.removeCallbacks(updateTimerRunnable)
                _playerState.value = PlayerState.Prepared
                playerStateEnum = STATE_PREPARED
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _playerState.value = PlayerState.Playing
        playerStateEnum = STATE_PLAYING
        handler.post(updateTimerRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _playerState.value = PlayerState.Paused
        playerStateEnum = STATE_PAUSED
        handler.removeCallbacks(updateTimerRunnable)
    }

    fun playbackControl() {
        when (playerStateEnum) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    fun onPause() {
        if (playerStateEnum == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(updateTimerRunnable)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_TIMER_DELAY = 300L

        fun getFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val playerInteractor = Creator.providePlayerInteractor()
                PlayerViewModel(playerInteractor, url)
            }
        }
    }
}