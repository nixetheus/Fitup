package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityHomeBinding
import it.polimi.mobile.design.entities.User
import it.polimi.mobile.design.enum.Gender
import it.polimi.mobile.design.helpers.HelperFunctions

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userDatabase = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextBtn.setOnClickListener { createUser() }
    }

    private fun createUser(){

        val uid = firebaseAuth.uid.toString()
        val username = binding.username.editText?.text.toString()
        val age = HelperFunctions().parseIntInput(binding.age.editText?.text.toString())
        val weight = HelperFunctions().parseFloatInput(binding.weight.editText?.text.toString())
        val gender = if (binding.gender.selectedItem == "Male") Gender.MALE else Gender.FEMALE

        if (username.isNotEmpty()) {
            val newUser = User(uid, username, weight, age ,gender, 0)
            userDatabase.child(uid).setValue(newUser).addOnSuccessListener {
                val intent = Intent(this, CentralActivity::class.java)
                startActivity(intent)
            }
        }
        else Toast.makeText(this, "Username is not valid", Toast.LENGTH_SHORT).show()

    }
}