package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {
    private val _themeState = MutableLiveData<ThemeSettings>()
    val themeState: LiveData<ThemeSettings> = _themeState

    init {
        _themeState.value = settingsInteractor.getThemeSettings()
    }

    fun onThemeSwitched(isDarkTheme: Boolean) {
        val newSettings = ThemeSettings(isDarkTheme = isDarkTheme)
        settingsInteractor.updateThemeSetting(newSettings)
        _themeState.value = newSettings
    }

    fun onShareAppClicked() {
        sharingInteractor.shareApp()
    }

    fun onOpenSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onOpenTermsClicked() {
        sharingInteractor.openTerms()
    }
}