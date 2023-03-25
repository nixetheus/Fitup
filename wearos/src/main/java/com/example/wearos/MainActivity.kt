package com.example.wearos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.wearos.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import java.util.concurrent.ExecutionException
//import it.polimi.mobile.design.entities.Workout



class MainActivity : Activity() /*, DataClient.OnDataChangedListener*/ {


    private var exerciseName: TextView? = null
    private var workoutName: TextView?= null


    private var talkButton: Button? = null

    var receivedMessageNumber = 1

    var sentMessageNumber = 1

    private lateinit var binding: ActivityMainBinding

    protected var myHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        talkButton=binding.talkClick
        workoutName=binding.workoutName
        exerciseName=binding.exerciseName



        //Create an OnClickListener//
        talkButton!!.setOnClickListener {
            val onClickMessage = "I just sent the handheld a message " + sentMessageNumber++
            exerciseName!!.text = onClickMessage

//Use the same path//
            val datapath = "/my_path"
            SendMessage(datapath, onClickMessage).start()
        }
        val newFilter = IntentFilter(Intent.ACTION_SEND)

        val messageReceiver = Receiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);



    }





    inner class Receiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {

//Display the following when a new message is received//
            val onMessageReceived =
                intent?.extras?.get("message") as String
            workoutName?.text = "ready for ${onMessageReceived} workout?"
            talkButton?.visibility =View.VISIBLE
        }
    }


    inner class SendMessage     //Constructor for sending information to the Data Layer//
        (var path: String, var message: String) : Thread() {





        override fun run() {

//Retrieve the connected devices//
            val nodeListTask: Task<List<Node>> =
                Wearable.getNodeClient(applicationContext).connectedNodes
            try {

//Block on a task and get the result synchronously//
                val nodes: List<Node> = Tasks.await<List<Node>>(nodeListTask)
                for (node in nodes) {

//Send the message///
                    val sendMessageTask: Task<Int> = Wearable.getMessageClient(this@MainActivity)
                        .sendMessage(node.id, path, message.toByteArray())
                    try {
                        val result = Tasks.await<Int>(sendMessageTask)


//Handle the errors//
                    } catch (exception: ExecutionException) {

//TO DO//
                    } catch (exception: InterruptedException) {

//TO DO//
                    }
                }
            } catch (exception: ExecutionException) {

//TO DO//
            } catch (exception: InterruptedException) {

//TO DO//
            }
        }
    }
    /*public override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }
    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }



    override fun onDataChanged(p0: DataEventBuffer) {
        TODO("Not yet implemented")
    }*/


}