package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SharingInteractor
import com.practicum.playlistmaker.domain.model.ThemeSettings

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsInteractor: SettingsInteractor = Creator.provideSettingsInteractor(this)
        val sharingInteractor: SharingInteractor = Creator.provideSharingInteractor(this)

        val backButton = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        backButton.setOnClickListener {
            finish()
        }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)

        themeSwitch.isChecked = settingsInteractor.getThemeSettings().isDarkTheme

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            settingsInteractor.updateThemeSetting(ThemeSettings(isDarkTheme = checked))
            (applicationContext as App).switchTheme(checked)
        }

        val shareButton = findViewById<LinearLayout>(R.id.share_button)
        shareButton.setOnClickListener {
            sharingInteractor.shareApp()
        }

        val supportButton = findViewById<LinearLayout>(R.id.support_button)
        supportButton.setOnClickListener {
            sharingInteractor.openSupport()
        }

        val agreementButton = findViewById<LinearLayout>(R.id.agreement_button)
        agreementButton.setOnClickListener {
            sharingInteractor.openTerms()
        }
    }
}