package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityHomeBinding
import it.polimi.mobile.design.entities.User
import it.polimi.mobile.design.enum.Gender

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
            createUser()
        }
    }
    private fun createUser(){
        val gender:Gender
        val uid = firebaseAuth.uid.toString()
        val username= binding.username.editText?.text.toString()
        val age= binding.age.editText?.text.toString()
        val weight= binding.weight.editText?.text.toString()
        val g=binding.gender.selectedItem.toString()

        gender = if (g=="Male")
            Gender.MALE
        else
            Gender.FEMALE
        if (username.isNotEmpty()&&age.isNotEmpty()&&weight.isNotEmpty()){

            database= FirebaseDatabase.getInstance().getReference("Users")
            val user=User(uid,username,weight, age ,gender, "0" )
            database.child(uid).setValue(user).addOnSuccessListener {
                Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CentralActivity::class.java)
                startActivity(intent)
        }
        }
        else Toast.makeText(this, "Fill in all fields to continue!!", Toast.LENGTH_SHORT).show()

    }
}