package it.polimi.mobile.design

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import it.polimi.mobile.design.databinding.ActivityLogoutPopUpBinding

class LogoutPopUp : Activity() {

    private lateinit var binding: ActivityLogoutPopUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLogoutPopUpBinding.inflate(layoutInflater)
        overridePendingTransition(0, 0)
        setContentView(binding.root)
        createBindings()
    }

    private fun createBindings() {
        binding.yesBtn.setOnClickListener {

            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("925709693638-nm3agj53a3frj6invj8ob7tu63ot5f7r.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(application, options)
            googleClient.signOut()
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.noBtn.setOnClickListener {
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }
    }
}