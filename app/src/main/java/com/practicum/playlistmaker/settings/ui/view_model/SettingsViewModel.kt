package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
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

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

                SettingsViewModel(
                    sharingInteractor = Creator.provideSharingInteractor(application),
                    settingsInteractor = Creator.provideSettingsInteractor(application)
                )
            }
        }
    }
}