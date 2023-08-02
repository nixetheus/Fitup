package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.polimi.mobile.design.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityResetPasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createBindings()
    }

    private fun createBindings() {

        binding.resetBtn.setOnClickListener{
            val sPass = binding.resetEmailField.text.toString()
            mAuth.sendPasswordResetEmail(sPass).addOnSuccessListener {
                Toast.makeText(this, "Please check your e-mail", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, it.toString(),Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginResetBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}