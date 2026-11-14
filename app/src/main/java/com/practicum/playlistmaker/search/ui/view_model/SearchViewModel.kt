package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.HistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchState

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = _stateLiveData

    private var latestSearchText: String? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        val history = historyInteractor.getHistory()
        if (history.isNotEmpty()) {
            _stateLiveData.value = SearchState.History(history)
        }
    }

    fun addTrackToHistory(track: Track) {
        historyInteractor.addTrack(track)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _stateLiveData.value = SearchState.Content(emptyList())
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        latestSearchText = changedText
        searchTracks(changedText)
    }

    fun searchTracks(searchText: String) {
        if (searchText.isBlank()) {
            return
        }

        _stateLiveData.value = SearchState.Loading

        tracksInteractor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>?, errorMessage: String?) {
                val foundTracks = mutableListOf<Track>()
                if (tracks != null) {
                    foundTracks.addAll(tracks)
                }

                when {
                    errorMessage != null -> {
                        _stateLiveData.postValue(SearchState.Error)
                    }
                    foundTracks.isEmpty() -> {
                        _stateLiveData.postValue(SearchState.Empty)
                    }
                    else -> {
                        _stateLiveData.postValue(SearchState.Content(foundTracks))
                    }
                }
            }
        })
    }

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                SearchViewModel(
                    tracksInteractor = Creator.provideTracksInteractor(application),
                    historyInteractor = Creator.provideHistoryInteractor(application)
                )
            }
        }
    }
}