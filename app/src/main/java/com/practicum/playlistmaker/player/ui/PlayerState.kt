package com.practicum.playlistmaker.player.ui

sealed interface PlayerState {
    object Default : PlayerState
    object Prepared : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState

    data class Progress(val progress: String) : PlayerState
}