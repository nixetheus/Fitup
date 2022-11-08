package it.polimi.mobile.design

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.ActivityHomeBinding
import it.polimi.mobile.design.databinding.ActivityMainBinding

class CentralActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCentralBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCentralBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}