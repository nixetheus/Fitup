package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mAuth = FirebaseAuth.getInstance()
    private var userDatabase = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Leggi l'impostazione della lingua salvata nelle SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val languageCode = sharedPreferences.getString("language", "")

        // Se è presente una lingua salvata nelle SharedPreferences, imposta la lingua
        if (!languageCode.isNullOrEmpty()) {
            setLocale(languageCode)
        }

        createBindings()
        checkUserSignIn()
    }
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun createBindings() {
        binding.SignInBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserSignIn() {
        mAuth.currentUser?.let {
            val uid = mAuth.uid.toString()
            userDatabase.child(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    val intent = Intent(this, CentralActivity::class.java)
                    startActivity(intent)
                } else {
                    binding.loadingOverlay.visibility = View.GONE
                }
            }
        }
        if (mAuth.currentUser == null)
            binding.loadingOverlay.visibility = View.GONE
    }
}


