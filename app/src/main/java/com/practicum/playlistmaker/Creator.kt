package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.dto.TrackAPI
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.SharingRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private fun getSettingsRepository(context: Context): SettingsRepository {
        val sharedPrefs = context.getSharedPreferences("practicum_playlist_maker", Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPrefs)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractorImpl {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractorImpl {
        return SharingInteractorImpl(getSharingRepository(context))
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
        val trackApiService = retrofit.create(TrackAPI::class.java)

        return TracksRepositoryImpl(trackApiService)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun providePlayerInteractor(): com.practicum.playlistmaker.domain.api.PlayerInteractor {
        return com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl(PlayerRepositoryImpl())
    }
}