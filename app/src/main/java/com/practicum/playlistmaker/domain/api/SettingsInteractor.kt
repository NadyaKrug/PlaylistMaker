package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}
