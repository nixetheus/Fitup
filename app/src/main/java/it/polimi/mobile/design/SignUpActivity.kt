package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.polimi.mobile.design.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createBindings()
    }

    private fun createBindings() {

        binding.Pass2.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN) {
                binding.SignUpBtn.performClick();
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.loginSignup.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.SignUpBtn.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val email = binding.EmailField.text.toString()
        val pass = binding.Pass.text.toString()
        val confirmPass = binding.Pass2.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty() && pass == confirmPass) {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please fill in all the fields correctly.", Toast.LENGTH_SHORT).show()
        }
    }
}