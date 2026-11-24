package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PRACTICUM_PLAYLIST_MAKER = "practicum_playlist_maker"
const val DARK_THEME_KEY = "dark_theme_key"

class App : Application() {

    var darkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, viewModelModule)
        }

        sharedPrefs = getSharedPreferences(PRACTICUM_PLAYLIST_MAKER, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnable: Boolean) {
        darkTheme = darkThemeEnable

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnable) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, darkTheme)
            .apply()
    }
}