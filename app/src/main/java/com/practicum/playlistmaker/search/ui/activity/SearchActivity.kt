package com.practicum.playlistmaker.search.ui.activity

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.ui.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchAdapter: Adapter
    private lateinit var historyAdapter: Adapter
    private lateinit var historyLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNothingFound: View
    private lateinit var placeholderServerError: View
    private lateinit var editText: EditText
    private lateinit var progressBar: ProgressBar

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        viewModel = ViewModelProvider(
            this,
            SearchViewModel.Companion.getFactory()
        )[SearchViewModel::class.java]

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
                viewModel.addTrackToHistory(track)
                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra("TRACK", track)
                }
                startActivity(intent)
            }
        }

        historyAdapter = Adapter { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
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

        viewModel.observeState().observe(this) { state ->
            renderState(state)
        }

        toolBar.setNavigationOnClickListener {
            finish()
        }

        clear.setOnClickListener {
            editText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
            viewModel.loadHistory()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editText.text.isEmpty()) {
                viewModel.loadHistory()
            } else {
                historyLayout.visibility = View.GONE
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clear.visibility = clearButtonVisibility(s)
                val text = s.toString()
                if (editText.hasFocus() && text.isEmpty()) {
                    viewModel.loadHistory()
                } else {
                    viewModel.searchDebounce(text)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        editText.addTextChangedListener(textWatcher)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = editText.text.toString()
                if (text.isNotBlank()) {
                    viewModel.searchTracks(text)
                }
                true
            } else {
                false
            }
        }

        refreshButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotBlank()) {
                viewModel.searchTracks(text)
            }
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Loading -> {
                progressBar.isVisible = true
                recyclerView.isVisible = false
                placeholderNothingFound.isVisible = false
                placeholderServerError.isVisible = false
                historyLayout.isVisible = false
            }
            is SearchState.Content -> {
                progressBar.isVisible = false
                recyclerView.isVisible = true
                placeholderNothingFound.isVisible = false
                placeholderServerError.isVisible = false
                historyLayout.isVisible = false

                searchAdapter.tracks.clear()
                searchAdapter.tracks.addAll(state.tracks)
                searchAdapter.notifyDataSetChanged()
            }
            is SearchState.Empty -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                placeholderNothingFound.isVisible = true
                placeholderServerError.isVisible = false
                historyLayout.isVisible = false

                searchAdapter.tracks.clear()
                searchAdapter.notifyDataSetChanged()
            }
            is SearchState.Error -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                placeholderNothingFound.isVisible = false
                placeholderServerError.isVisible = true
                historyLayout.isVisible = false

                searchAdapter.tracks.clear()
                searchAdapter.notifyDataSetChanged()
            }
            is SearchState.History -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                placeholderNothingFound.isVisible = false
                placeholderServerError.isVisible = false
                historyLayout.isVisible = editText.hasFocus()

                if (historyLayout.isVisible) {
                    historyAdapter.tracks = ArrayList(state.historyTracks)
                    historyAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}