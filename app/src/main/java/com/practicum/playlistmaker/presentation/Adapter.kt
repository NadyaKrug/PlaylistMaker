package com.practicum.playlistmaker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ViewHolder
import com.practicum.playlistmaker.domain.model.Track

class Adapter(private val onTrackClickListener: (Track) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

     var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClickListener(track)
        }

    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}