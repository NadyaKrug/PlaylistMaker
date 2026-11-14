package com.practicum.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.view_model.MainViewModel
import com.practicum.playlistmaker.player.ui.activity.MusicActivity
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val musicButton = findViewById<Button>(R.id.music_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


        searchButton.setOnClickListener{
            viewModel.onSearchClicked()
        }

        musicButton.setOnClickListener {

            viewModel.onMediaLibraryClicked()
        }

        settingsButton.setOnClickListener {

            viewModel.onSettingsClicked()
        }

        viewModel.navigateToSearch.observe(this) {
            if (it != null) {
                startActivity(Intent(this, SearchActivity::class.java))
                viewModel.onSearchNavigated()
            }
        }
        viewModel.navigateToSettings.observe(this) {
            if (it != null) {
                startActivity(Intent(this, SettingsActivity::class.java))
                viewModel.onSettingsNavigated()
            }
        }
        viewModel.navigateToMediaLibrary.observe(this) {
            if (it != null) {
                startActivity(Intent(this, MusicActivity::class.java))
                viewModel.onMediaLibraryNavigated()
            }
        }

    }



}