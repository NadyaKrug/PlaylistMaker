package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

const val PRACTICUM_PLAYLIST_MAKER = "playlist_maker_preferences"
const val DARK_THEME_KEY = "dark_theme_key"

class App : Application() {

    var darkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(){
        super.onCreate()
        sharedPrefs = getSharedPreferences(PRACTICUM_PLAYLIST_MAKER,MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        switchTheme(darkTheme)

    }

    fun switchTheme (darkThemeEnable : Boolean){
        darkTheme = darkThemeEnable

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnable){
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