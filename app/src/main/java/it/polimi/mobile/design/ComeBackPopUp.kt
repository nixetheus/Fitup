package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.polimi.mobile.design.databinding.ComeBackPopUpBinding

class ComeBackPopUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val popup = ComeBackPopUpBinding.inflate(layoutInflater)
        overridePendingTransition(0, 0)
        setContentView(popup.root)

        popup.yesBtn.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }
        popup.noBtn.setOnClickListener{
            finish()
        }
    }
}
