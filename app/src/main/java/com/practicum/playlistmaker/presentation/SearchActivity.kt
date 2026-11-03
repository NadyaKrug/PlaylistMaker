package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.model.Track

class SearchActivity : AppCompatActivity() {
    private var searchText: String = SEARCH_TEXT_DEF

    private lateinit var searchAdapter: Adapter
    private lateinit var historyAdapter: Adapter
    private lateinit var historyInteractor: HistoryInteractor
    private lateinit var historyLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNothingFound: View
    private lateinit var placeholderServerError: View
    private lateinit var editText: EditText
    private lateinit var progressBar: ProgressBar

    private val tracksInteractor = Creator.provideTracksInteractor(this)

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { doSearch() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        historyInteractor = Creator.provideHistoryInteractor(this)

        historyLayout = findViewById(R.id.history_layout)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.history_recycler_view)
        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        editText = findViewById(R.id.search_edit_text)
        val clear = findViewById<ImageView>(R.id.clear_icon)
        val toolBar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        recyclerView = findViewById<RecyclerView>(R.id.track_recycler_view)
        placeholderNothingFound = findViewById<View>(R.id.nothing_found)
        placeholderServerError = findViewById<View>(R.id.server_error)
        val refreshButton = findViewById<Button>(R.id.refresh_button)
        progressBar = findViewById(R.id.progressBar)

        searchAdapter = Adapter { track ->
            if (clickDebounce()) {
                historyInteractor.addTrack(track)
                historyAdapter.tracks = ArrayList(historyInteractor.getHistory())
                historyAdapter.notifyDataSetChanged()

                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra("TRACK", track)
                }
                startActivity(intent)
            }
        }

        historyAdapter = Adapter { track ->
            if (clickDebounce()) {
                historyInteractor.addTrack(track)
                historyAdapter.tracks = ArrayList(historyInteractor.getHistory())
                historyAdapter.notifyDataSetChanged()

                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra("TRACK", track)
                }
                startActivity(intent)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchAdapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        toolBar.setNavigationOnClickListener {
            finish()
        }

        clear.setOnClickListener {
            handler.removeCallbacks(searchRunnable)
            editText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)

            searchAdapter.tracks.clear()
            searchAdapter.notifyDataSetChanged()
            showSuccess(recyclerView, placeholderNothingFound, placeholderServerError)

            val historyList = historyInteractor.getHistory()
            val hasHistory = editText.hasFocus() && historyList.isNotEmpty()
            historyLayout.isVisible = hasHistory

            if (hasHistory) {
                historyAdapter.tracks = ArrayList(historyInteractor.getHistory())
                historyAdapter.notifyDataSetChanged()
            }
        }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            historyAdapter.tracks.clear()
            historyAdapter.notifyDataSetChanged()
            historyLayout.visibility = View.GONE
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            val historyList = historyInteractor.getHistory()
            if (hasFocus && editText.text.isEmpty() && historyList.isNotEmpty()) {
                historyAdapter.tracks = ArrayList(historyInteractor.getHistory())
                historyAdapter.notifyDataSetChanged()
                historyLayout.visibility = View.VISIBLE
            } else {
                historyLayout.visibility = View.GONE
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clear.visibility = clearButtonVisibility(s)
                searchText = s.toString()

                val historyList = historyInteractor.getHistory()
                if (editText.hasFocus() && s.isNullOrEmpty() && historyList.isNotEmpty()) {
                    historyAdapter.tracks = ArrayList(historyInteractor.getHistory())
                    historyAdapter.notifyDataSetChanged()
                    historyLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    placeholderNothingFound.visibility = View.GONE
                    placeholderServerError.visibility = View.GONE
                    progressBar.isVisible = false
                    handler.removeCallbacks(searchRunnable)
                } else {
                    historyLayout.visibility = View.GONE
                    searchDebounce()
                }
            }
        }

        editText.addTextChangedListener(textWatcher)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                historyLayout.visibility = View.GONE
                handler.removeCallbacks(searchRunnable)
                doSearch()
                true
            } else {
                false
            }
        }

        refreshButton.setOnClickListener {
            doSearch()
        }
    }

    private fun doSearch() {
        if (searchText.trim().isEmpty()) return
        progressBar.isVisible = true
        recyclerView.isVisible = false
        placeholderNothingFound.isVisible = false
        placeholderServerError.isVisible = false

        tracksInteractor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>?, errorMessage: String?) {
                runOnUiThread {
                    progressBar.isVisible = false

                    if (!errorMessage.isNullOrEmpty()) {
                        showServerError(recyclerView, placeholderNothingFound, placeholderServerError)
                    } else if (tracks != null && tracks.isNotEmpty()) {
                        searchAdapter.tracks.clear()
                        searchAdapter.tracks.addAll(tracks)
                        searchAdapter.notifyDataSetChanged()
                        showSuccess(recyclerView, placeholderNothingFound, placeholderServerError)
                    } else {
                        showEmptyResult(recyclerView, placeholderNothingFound, placeholderServerError)
                    }
                }
            }
        })
    }

    private fun showSuccess(
        recyclerView: View,
        placeholderNothingFound: View,
        placeholderServerError: View
    ) {
        recyclerView.visibility = View.VISIBLE
        placeholderNothingFound.visibility = View.GONE
        placeholderServerError.visibility = View.GONE
    }

    private fun showEmptyResult(
        recyclerView: View,
        placeholderNothingFound: View,
        placeholderServerError: View
    ) {
        searchAdapter.tracks.clear()
        searchAdapter.notifyDataSetChanged()
        recyclerView.visibility = View.GONE
        placeholderNothingFound.visibility = View.VISIBLE
        placeholderServerError.visibility = View.GONE
    }

    private fun showServerError(
        recyclerView: View,
        placeholderNothingFound: View,
        placeholderServerError: View
    ) {
        searchAdapter.tracks.clear()
        searchAdapter.notifyDataSetChanged()
        recyclerView.visibility = View.GONE
        placeholderNothingFound.visibility = View.GONE
        placeholderServerError.visibility = View.VISIBLE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_TEXT_DEF)
        val editText = findViewById<EditText>(R.id.search_edit_text)
        editText.setText(searchText)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_TEXT_DEF = ""

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}