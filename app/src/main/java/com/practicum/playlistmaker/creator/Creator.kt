package com.practicum.playlistmaker.creator

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.data.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TrackAPI
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.HistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private fun getSettingsRepository(context: Context): SettingsRepository {
        val sharedPrefs = context.getSharedPreferences("practicum_playlist_maker", Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPrefs)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        val externalNavigator = ExternalNavigator(context)
        return SharingInteractorImpl(
            externalNavigator = externalNavigator,
            shareAppLink = context.getString(R.string.share_app_text),
            termsLink = context.getString(R.string.practicum_offer_url),
            supportEmailData = EmailData(
                email = context.getString(R.string.support_email),
                subject = context.getString(R.string.support_email_subject),
                text = context.getString(R.string.support_email_text)
            )
        )
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val trackApiService = retrofit.create(TrackAPI::class.java)

        return TracksRepositoryImpl(trackApiService)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        val sharedPrefs = context.getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
        val historyRepository = HistoryRepositoryImpl(sharedPrefs, Gson())
        return HistoryInteractorImpl(historyRepository)
    }
}