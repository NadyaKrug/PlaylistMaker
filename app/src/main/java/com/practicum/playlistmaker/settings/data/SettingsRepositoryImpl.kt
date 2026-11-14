package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences): SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        return ThemeSettings(isDarkTheme = isDark)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY,settings.isDarkTheme).apply()
    }

    companion object{
        private const val DARK_THEME_KEY = "dark_theme_key"
    }

}