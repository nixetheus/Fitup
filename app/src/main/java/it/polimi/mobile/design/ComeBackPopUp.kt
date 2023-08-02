package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.polimi.mobile.design.databinding.ComeBackPopUpBinding

class ComeBackPopUp : AppCompatActivity() {

    private lateinit var binding: ComeBackPopUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ComeBackPopUpBinding.inflate(layoutInflater)
        overridePendingTransition(0, 0)
        setContentView(binding.root)

        createBindings()
    }

    private fun createBindings() {
        binding.yesBtn.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }

        binding.noBtn.setOnClickListener{
            finish()
        }
    }
}
