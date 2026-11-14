package com.practicum.playlistmaker.player.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var playButton: ImageButton
    private lateinit var playBackTime: TextView

    private lateinit var albumGroup: View
    private lateinit var yearGroup: View
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = intent.getSerializableExtra("TRACK") as? Track
        val previewUrl = track?.previewUrl ?: ""

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(previewUrl)
        )[PlayerViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val cover = findViewById<ImageView>(R.id.cover)
        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val durationValue = findViewById<TextView>(R.id.duration_value)
        albumValue = findViewById(R.id.album_value)
        albumGroup = findViewById(R.id.album_group)
        yearValue = findViewById(R.id.year_value)
        yearGroup = findViewById(R.id.year_group)
        val genreValue = findViewById<TextView>(R.id.genre_value)
        val countryValue = findViewById<TextView>(R.id.country_value)
        playButton = findViewById(R.id.play_button)
        playBackTime = findViewById(R.id.playback_time)

        toolbar.setNavigationOnClickListener { finish() }

        if (track != null) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            val millis = track.trackTimeMillis.toLongOrNull() ?: 0L
            durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
            albumGroup.isVisible = !track.collectionName.isNullOrEmpty()
            if (albumGroup.isVisible) albumValue.text = track.collectionName

            val year = track.getRealeasedDate()
            yearGroup.isVisible = (year != null)
            if (yearGroup.isVisible) yearValue.text = year

            genreValue.text = track.primaryGenreName
            countryValue.text = track.country

            val pxRadius = dpToPx(8f, this)
            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_nothing_found_120)
                .centerCrop()
                .transform(RoundedCorners(pxRadius))
                .into(cover)
        }

        playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.playerState.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                playButton.isEnabled = false
                playButton.setImageResource(R.drawable.ic_play_button_100_100)
                playBackTime.text = "00:00"
            }
            is PlayerState.Prepared -> {
                playButton.isEnabled = true
                playButton.setImageResource(R.drawable.ic_play_button_100_100)
                playBackTime.text = "00:00"
            }
            is PlayerState.Playing -> {
                playButton.setImageResource(R.drawable.ic_stop_83_83)
            }
            is PlayerState.Paused -> {
                playButton.setImageResource(R.drawable.ic_play_button_100_100)
            }
            is PlayerState.Progress -> {
                playBackTime.text = state.progress
            }
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}