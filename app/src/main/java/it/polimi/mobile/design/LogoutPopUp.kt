package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityLogoutPopUpBinding

class LogoutPopUp : AppCompatActivity() {
    private lateinit var binding: ActivityLogoutPopUpBinding
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutPopUpBinding.inflate(layoutInflater)
        overridePendingTransition(0, 0)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.uid.toString()
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(uid).get().addOnSuccessListener {
            if (it.exists()) {
                val username = it.child("username").value
                binding.popupWindowTitle.text = username.toString()
            }
        }
        binding.yesBtn.setOnClickListener{
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("925709693638-nm3agj53a3frj6invj8ob7tu63ot5f7r.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(application, options)
            googleClient.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.noBtn.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }
    }
}