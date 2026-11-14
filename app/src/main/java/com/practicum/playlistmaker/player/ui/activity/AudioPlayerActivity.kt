package com.practicum.playlistmaker.player.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var cover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var albumGroup: View
    private lateinit var yearValue: TextView
    private lateinit var yearGroup: View
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var playButton: ImageButton
    private lateinit var playBackTime: TextView

    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private var previewUrl: String? = null

    private val playerInteractor = Creator.providePlayerInteractor()

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                playBackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(playerInteractor.getCurrentPosition())
                handler.postDelayed(this, REFRESH_TIMER_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        toolbar = findViewById(R.id.toolbar)
        cover = findViewById(R.id.cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        durationValue = findViewById(R.id.duration_value)
        albumValue = findViewById(R.id.album_value)
        albumGroup = findViewById(R.id.album_group)
        yearValue = findViewById(R.id.year_value)
        yearGroup = findViewById(R.id.year_group)
        genreValue = findViewById(R.id.genre_value)
        countryValue = findViewById(R.id.country_value)
        playButton = findViewById(R.id.play_button)
        playBackTime = findViewById(R.id.playback_time)

        playButton.setOnClickListener {
            playBackControl()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra("TRACK") as? Track
        if (track != null) {
            bindTrack(track)
        }
    }

    private fun bindTrack(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        val millis = track.trackTimeMillis.toLongOrNull() ?: 0L
        durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
        albumGroup.isVisible = !track.collectionName.isNullOrEmpty()
        if (albumGroup.isVisible) {
            albumValue.text = track.collectionName
        }
        val year = track.getRealeasedDate()
        yearGroup.isVisible = (year != null)
        if (yearGroup.isVisible) {
            yearValue.text = year
        }
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        val pxRadius = dpToPx(8f, this)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_nothing_found_120)
            .centerCrop()
            .transform(RoundedCorners(pxRadius))
            .into(cover)

        previewUrl = track.previewUrl
        preparePlayer()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun preparePlayer() {
        if (previewUrl.isNullOrEmpty()) return

        playerInteractor.preparePlayer(
            url = previewUrl!!,
            onPrepared = {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
            },
            onCompletion = {
                playerState = STATE_PREPARED
                handler.removeCallbacks(updateTimerRunnable)
                playButton.setImageResource(R.drawable.ic_play_button_100_100)
                playBackTime.text = "00:00"
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playButton.setImageResource(R.drawable.ic_stop_83_83)
        playerState = STATE_PLAYING
        handler.post(updateTimerRunnable)
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        playButton.setImageResource(R.drawable.ic_play_button_100_100)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimerRunnable)
    }

    private fun playBackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(updateTimerRunnable)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val REFRESH_TIMER_DELAY = 300L
    }
}