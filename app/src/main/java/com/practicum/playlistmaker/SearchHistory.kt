package com.practicum.playlistmaker

import android.content.SharedPreferences

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


const val SEARCH_HISTORY_KEY = "search_history_key"

private const val TRACK_SIZE = 10
class SearchHistory (private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun read() : ArrayList<Track>{
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return arrayListOf()
        val tracks = gson.fromJson(json, Array<Track>::class.java) ?: return arrayListOf()
        return tracks.toCollection(ArrayList())

    }

    fun write(tracks: ArrayList<Track>){
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json).apply()
    }

    fun addTrack(track : Track){
        val history = read()
        history.removeAll{it.trackId == track.trackId}
        history.add(0, track)
        if (history.size > TRACK_SIZE ){
            history.removeAt(history.size -1)
        }
        write(history)


    }

    fun clear(){
        sharedPreferences.edit()

            .remove(SEARCH_HISTORY_KEY).apply()
    }


}