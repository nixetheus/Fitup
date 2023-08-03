package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import it.polimi.mobile.design.databinding.SettingsActivityBinding
import java.util.Locale


class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private lateinit var binding: SettingsActivityBinding
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings, SettingsFragment()).commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Puoi salvare la lingua selezionata nelle SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putString("language", languageCode).apply()

        // Ricarica l'activity per applicare le modifiche della lingua
        recreate()
    }



    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val logout: EditTextPreference? = findPreference("logout")
            val nickname: EditTextPreference? = findPreference("nickname")
            val languagePreference: ListPreference? = findPreference("language")

            languagePreference?.setOnPreferenceChangeListener { preference, newValue ->
                val languageCode = newValue as String
                (activity as? SettingsActivity)?.setLocale(languageCode)
                true
            }
            if (logout != null) {
                logout.setOnPreferenceClickListener {
                    val intent = Intent(this.context, LogoutPopUp::class.java)
                    startActivity(intent)
                    true
                }
            }


        }

    }


    private fun setupLanguages() {
        /*val languagePref = applicationContext.getSharedPreferences("language", Context.MODE_PRIVATE) as DialogPreference
        languagePref.onPreferenceClickListener = Preference.OnPreferenceClickListener { // open browser or intent here
            setLocale("hello")
            true
        }*/
    }


    /*private fun setLocale(lang: String?) {

        Toast.makeText(applicationContext, lang, Toast.LENGTH_SHORT).show()
        /*val myLocale = lang?.let { Locale(it) }
        val res: Resources = resources
        val conf: Configuration = res.configuration
        conf.setLocale(myLocale)

        val refresh = Intent(this, SettingsActivity::class.java)

        finish()
        startActivity(refresh)*/
    }*/

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference?
    ): Boolean {
        val args = pref?.extras
        val fragment = pref?.let {
            supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                it.fragment)
        }
        if (fragment != null) {
            fragment.arguments = args
        }
        fragment?.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit()
        }
        return true

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
    }
}