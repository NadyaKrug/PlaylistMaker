package com.practicum.playlistmaker

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
                Toast.makeText( this@MainActivity, "Нажата кнопка Поиск", Toast.LENGTH_SHORT).show()
            }

        }
        searchButton.setOnClickListener(searchClickListener)

        musicButton.setOnClickListener { Toast.makeText(this@MainActivity, "Нажата кнопка Медиатека",
            Toast.LENGTH_SHORT).show() }

        settingsButton.setOnClickListener { Toast.makeText(this@MainActivity, "Нажата кнопка Настройки", Toast.LENGTH_SHORT).show() }

    }



}