package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SharingInteractor
import com.practicum.playlistmaker.domain.api.SharingRepository

class SharingInteractorImpl(private val sharingRepository: SharingRepository) : SharingInteractor {
    override fun shareApp() {
        sharingRepository.shareApp()
    }

    override fun openTerms() {
        sharingRepository.openTerms()
    }

    override fun openSupport() {
        sharingRepository.openSupport()
    }
}