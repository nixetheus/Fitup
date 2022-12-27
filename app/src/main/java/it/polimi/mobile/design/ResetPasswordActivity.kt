package it.polimi.mobile.design

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import it.polimi.mobile.design.R
import it.polimi.mobile.design.databinding.ActivityMainBinding
import it.polimi.mobile.design.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var database: DatabaseReference
    private lateinit var eMail:EditText
    private lateinit var reset: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)
        mAuth=FirebaseAuth.getInstance()
        eMail=binding.EmailField
        reset=binding.resetBtn
        reset.setOnClickListener{
            val sPass=eMail.text.toString()
            mAuth.sendPasswordResetEmail(sPass).addOnSuccessListener {
                Toast.makeText(this, "please check your e-mail", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener(){
                Toast.makeText(this, it.toString(),Toast.LENGTH_SHORT).show()
            }

        }
    }
}