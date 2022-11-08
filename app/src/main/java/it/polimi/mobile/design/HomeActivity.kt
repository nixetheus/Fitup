package it.polimi.mobile.design

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.entities.User
import it.polimi.mobile.design.databinding.ActivityHomeBinding
import it.polimi.mobile.design.enum.Gender
import java.time.LocalDateTime
import java.util.Date

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.nextBtn.setOnClickListener {
            val gender:Gender
            val uid = firebaseAuth.uid
            val username= binding.username.toString()
            val age= binding.age.toString()
            val weight= binding.weight.toString()
            val g=binding.gender.toString()
            val date=LocalDateTime.now()
            gender = if (g=="male")
                Gender.MALE
            else
                Gender.FEMALE

            database= FirebaseDatabase.getInstance().getReference("Users")
            val user=User(uid,username,weight, age,gender, date )
            database.child(username).setValue(user).addOnSuccessListener {
                Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}