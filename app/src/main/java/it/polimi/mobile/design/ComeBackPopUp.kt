package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import it.polimi.mobile.design.databinding.ComeBackPopUpBinding

class ComeBackPopUp : AppCompatActivity()
    {
        private lateinit var binding: ComeBackPopUpBinding
        private lateinit var database : DatabaseReference
        private lateinit var firebaseAuth: FirebaseAuth
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ComeBackPopUpBinding.inflate(layoutInflater)
            overridePendingTransition(0, 0)
            setContentView(binding.root)

            binding.yesBtn.setOnClickListener{

                val intent = Intent(this, CentralActivity::class.java)
                startActivity(intent)
            }
            binding.noBtn.setOnClickListener{
               onBackPressed()

            }

        }

}
