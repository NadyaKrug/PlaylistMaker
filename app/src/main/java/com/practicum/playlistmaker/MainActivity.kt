package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val musicButton = findViewById<Button>(R.id.music_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        val searchClickListener: View.OnClickListener = object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val searchIntent = Intent( this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }

        }
        searchButton.setOnClickListener(searchClickListener)

        musicButton.setOnClickListener {
            val musicIntent = Intent(this@MainActivity, MusicActivity::class.java)
        startActivity(musicIntent)}

        settingsButton.setOnClickListener {
            val settingIntent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(settingIntent)}

    }



}