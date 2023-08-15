package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.SettingsActivityBinding
import it.polimi.mobile.design.helpers.DatabaseHelper
import java.util.*


class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private lateinit var binding: SettingsActivityBinding

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


        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putString("language", languageCode).apply()


        recreate()
    }



    class SettingsFragment : PreferenceFragmentCompat() {
        private val helperDB = DatabaseHelper().getInstance()
        private var firebaseAuth = FirebaseAuth.getInstance()
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val logout: EditTextPreference? = findPreference("logout")

            val languagePreference: ListPreference? = findPreference("language")
            val usernamePreference: EditTextPreference? = findPreference("username")
            if (usernamePreference != null) {
                firebaseAuth.uid?.let { userId ->
                    helperDB.usersSchema.child(userId).get().addOnSuccessListener { userSnapshot ->
                        val user = helperDB.getUserFromSnapshot(userSnapshot)
                        if (user != null) {
                            usernamePreference.summaryProvider =
                                Preference.SummaryProvider<EditTextPreference> { preference ->
                                    "${user.username}"
                                }
                        }

                    }
                }

            }
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
            usernamePreference?.setOnPreferenceChangeListener { preference, newValue ->
                val newUsername = newValue as String

                saveTheNewUsername(newUsername)
                true

            }
        }
        private fun saveTheNewUsername(newUsername: String) {
            val userId = getCurrentUserId()

            if (userId != null) {

                val usersRef: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("Users")


                usersRef.child(userId).child("username").setValue(newUsername)
                    .addOnSuccessListener {

                        Toast.makeText(
                            context,
                            "Username updated!",
                            Toast.LENGTH_SHORT
                        ).show()
                        val centralActivityIntent = Intent(context, CentralActivity::class.java)
                        startActivity(centralActivityIntent)

                        activity?.finish()
                    }
                    .addOnFailureListener { e ->

                        Toast.makeText(
                            context,
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()

                    }
            } else {

                Toast.makeText(context, "Error: User is not authenticated.", Toast.LENGTH_SHORT)
                    .show()
            }




        }

        private fun getCurrentUserId(): String {
            val firebaseAuth = FirebaseAuth.getInstance()
            return firebaseAuth.uid.toString()

        }
    }

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