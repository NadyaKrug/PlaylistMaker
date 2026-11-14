package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSettings(settings)
    }
}