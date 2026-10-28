package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.model.ThemeSettings

class SettingsInteractorImpl (private val settingsRepository: SettingsRepository){
    fun getThemeSettings(): ThemeSettings{
        return settingsRepository.getThemeSettings()
    }

    fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSettings(settings)
    }
}