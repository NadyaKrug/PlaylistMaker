package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _navigateToSearch = MutableLiveData<Unit?>()
    val navigateToSearch: LiveData<Unit?> = _navigateToSearch

    private val _navigateToMediaLibrary = MutableLiveData<Unit?>()
    val navigateToMediaLibrary: LiveData<Unit?> = _navigateToMediaLibrary

    private val _navigateToSettings = MutableLiveData<Unit?>()
    val navigateToSettings: LiveData<Unit?> = _navigateToSettings

    fun onSearchClicked() {
        _navigateToSearch.value = Unit
    }

    fun onMediaLibraryClicked() {
        _navigateToMediaLibrary.value = Unit
    }

    fun onSettingsClicked() {
        _navigateToSettings.value = Unit
    }
    fun onSearchNavigated() {
        _navigateToSearch.value = null
    }

    fun onMediaLibraryNavigated() {
        _navigateToMediaLibrary.value = null
    }

    fun onSettingsNavigated() {
        _navigateToSettings.value = null
    }
}