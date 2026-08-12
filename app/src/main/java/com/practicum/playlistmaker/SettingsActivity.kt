package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import android.net.Uri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity :: class.java)
            startActivity(intent)
        }

        val shareButton = findViewById<LinearLayout>(R.id.share_button)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            startActivity(Intent.createChooser(shareIntent, null))
        }

        val supportButton = findViewById<LinearLayout>(R.id.support_button)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            startActivity(supportIntent)
        }

        val agreementButton = findViewById<LinearLayout>(R.id.agreement_button)
        agreementButton.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(getString(R.string.practicum_offer_url))
            startActivity(agreementIntent)
        }
    }
}