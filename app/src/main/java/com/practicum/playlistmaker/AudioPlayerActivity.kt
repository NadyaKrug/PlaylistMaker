package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}