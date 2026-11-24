package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    private val searchRunnable = Runnable {
        val currentText = latestSearchText
        if (!currentText.isNullOrBlank()) {
            searchTracks(currentText)
        }
    }
    private val handler = Handler(Looper.getMainLooper())

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
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}