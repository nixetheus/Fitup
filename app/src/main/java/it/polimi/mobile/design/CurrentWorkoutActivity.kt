package it.polimi.mobile.design

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.polimi.mobile.design.databinding.ActivityCurrentWorkoutBinding
import it.polimi.mobile.design.entities.Workout

class CurrentWorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentWorkoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCurrentWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val workout= intent.extras?.get("workout") as Workout

    }
}