package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mAuth = FirebaseAuth.getInstance()
    private var userDatabase = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createBindings()
        checkUserSignIn()
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
                val intent = Intent(this, if (it.exists()) CentralActivity::class.java else HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}


