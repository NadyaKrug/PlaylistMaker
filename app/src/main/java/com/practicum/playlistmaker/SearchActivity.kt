package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {
    private var searchText: String  =  SEARCH_TEXT_DEF

    private val retrofit = Retrofit.Builder()
        .baseUrl(SEARCH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackApiService = retrofit.create<TrackAPI>()
    private val trackList = ArrayList<TrackInf>()
    private val trackAdapter = Adapter(trackList)



    override fun onCreate(savedInstanceState: Bundle?, ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val editText = findViewById<EditText>(R.id.search_edit_text)
        val clear = findViewById<ImageView>(R.id.clear_icon)
        val toolBar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        val recyclerView = findViewById<RecyclerView>(R.id.track_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter
        val placeholderNothingFound = findViewById<View>(R.id.nothing_found)
        val placeholderServerError = findViewById<View>(R.id.server_error)
        val refreshButton = findViewById<Button>(R.id.refresh_button)




        toolBar.setNavigationOnClickListener {
            finish()
        }

        clear.setOnClickListener {
            editText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)

            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            showSuccess(recyclerView, placeholderNothingFound, placeholderServerError)

        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clear.visibility = clearButtonVisibility(s)

                searchText = s.toString()
            }
        }

        editText.addTextChangedListener(textWatcher)
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doSearch(recyclerView, placeholderNothingFound, placeholderServerError)
                true
            } else {
                false
            }
        }
        refreshButton.setOnClickListener {
            doSearch(recyclerView, placeholderNothingFound, placeholderServerError)
        }

    }

    private fun doSearch(
        recyclerView: View,
        placeholderNothingFound: View,
        placeholderServerError: View
    ) {
        if (searchText.trim().isEmpty()) return

        trackApiService.search(searchText).enqueue(object : Callback<TrackResponse>{
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.code() == 200) {
                    trackList.clear()
                    val results = response.body()?.results
                    if (!results.isNullOrEmpty()) {
                        trackList.addAll(results)
                        trackAdapter.notifyDataSetChanged()
                        showSuccess(recyclerView, placeholderNothingFound, placeholderServerError)
                    } else {
                        showEmptyResult(recyclerView, placeholderNothingFound, placeholderServerError)
                    }
                } else {
                    showServerError(recyclerView, placeholderNothingFound, placeholderServerError)
                }
            }

             override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showServerError(recyclerView, placeholderNothingFound, placeholderServerError)
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
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        recyclerView.visibility = View.GONE
        placeholderNothingFound.visibility = View.VISIBLE
        placeholderServerError.visibility = View.GONE
    }

    private fun showServerError(
        recyclerView: View,
        placeholderNothingFound: View,
        placeholderServerError: View
    ) {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
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

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_TEXT_DEF = ""
        private const val SEARCH_BASE_URL = "https://itunes.apple.com"
    }

}