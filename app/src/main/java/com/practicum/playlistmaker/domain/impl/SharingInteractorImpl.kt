package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SharingRepository

class SharingInteractorImpl (private val sharingRepository: SharingRepository) {

    fun shareApp(){
        sharingRepository.shareApp()
    }

    fun openTerms(){
        sharingRepository.openTerms()
    }

    fun openSupport(){
        sharingRepository.openSupport()
    }
}