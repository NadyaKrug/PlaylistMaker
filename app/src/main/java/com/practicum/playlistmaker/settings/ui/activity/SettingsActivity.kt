package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory())[SettingsViewModel::class.java]

        val backButton = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        backButton.setOnClickListener {
            finish()
        }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)
        viewModel.themeState.observe(this) { themeSettings ->
            themeSwitch.isChecked = themeSettings.isDarkTheme
            (applicationContext as App).switchTheme(themeSettings.isDarkTheme)
        }

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeSwitched(checked)
        }

        val shareButton = findViewById<LinearLayout>(R.id.share_button)
        shareButton.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        val supportButton = findViewById<LinearLayout>(R.id.support_button)
        supportButton.setOnClickListener {
            viewModel.onOpenSupportClicked()
        }

        val agreementButton = findViewById<LinearLayout>(R.id.agreement_button)
        agreementButton.setOnClickListener {
            viewModel.onOpenTermsClicked()
        }
    }
}