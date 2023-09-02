package it.polimi.mobile.design.wearos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.wearos.R

class EndWorkoutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_workout)
    }
    inner class Receiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {

//Display the following when a new message is received//
            if (intent?.extras?.get("workout")!=null){
                val onMessageReceived =
                    intent?.extras?.get("workout") as String
                val intent1 = Intent(this@EndWorkoutActivity, MainActivity::class.java)
                intent1.putExtra("workout", onMessageReceived)
                startActivity(intent1)
                }

        }
    }
}