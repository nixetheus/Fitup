package it.polimi.mobile.design

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.DialogPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.SettingsActivityBinding
import java.util.*


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings, SettingsFragment()).commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupLanguages()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    private fun setupLanguages() {
        /*val languagePref = applicationContext.getSharedPreferences("language", Context.MODE_PRIVATE) as DialogPreference
        languagePref.onPreferenceClickListener = Preference.OnPreferenceClickListener { // open browser or intent here
            setLocale("hello")
            true
        }*/
    }

    private fun setLocale(lang: String?) {

        Toast.makeText(applicationContext, lang, Toast.LENGTH_SHORT).show()
        /*val myLocale = lang?.let { Locale(it) }
        val res: Resources = resources
        val conf: Configuration = res.configuration
        conf.setLocale(myLocale)

        val refresh = Intent(this, SettingsActivity::class.java)

        finish()
        startActivity(refresh)*/
    }
}