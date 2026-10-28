package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker.DARK_THEME_KEY
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.model.ThemeSettings

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