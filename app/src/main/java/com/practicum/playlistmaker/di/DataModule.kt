package com.practicum.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.data.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TrackAPI
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.HistoryRepository
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val SETTINGS_PREFS = "practicum_playlist_maker"
private const val HISTORY_PREFS = "playlist_maker_preferences"

val dataModule = module {

    single<TrackAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackAPI::class.java)
    }

    single(named(SETTINGS_PREFS)) {
        androidContext().getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
    }

    single(named(HISTORY_PREFS)) {
        androidContext().getSharedPreferences(HISTORY_PREFS, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single { ExternalNavigator(androidContext()) }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(
            sharedPreferences = get(named(HISTORY_PREFS)),
            gson = get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            sharedPreferences = get(named(SETTINGS_PREFS))
        )
    }
}