package it.polimi.mobile.design

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.ActivityHomeBinding
import it.polimi.mobile.design.databinding.ActivityMainBinding

class CentralActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCentralBinding
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCentralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        val uid = firebaseAuth.uid.toString()
        database= FirebaseDatabase.getInstance().getReference("Users")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val username=it.child("username").value
                binding.textView4.text="Welcome "+username.toString()
                binding.textView2.text="Age="+it.child("age").value.toString()+
                "/n Weight="+it.child("weight").value.toString()+
                "/n Gender="+it.child("gender").value.toString()
            }
        }
    }
}