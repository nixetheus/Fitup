package com.example.wearos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log

import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.wearos.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import java.util.concurrent.ExecutionException
import kotlin.properties.Delegates

class MainActivity : Activity() , SensorEventListener {

    private var exerciseName: TextView? = null
    private var workoutName: TextView?= null
    private var sensorManager: SensorManager? = null
    private var mHeartSensor: Sensor? = null
    private lateinit var chrono: Chronometer
    private var timeWhenStopped by Delegates.notNull<Long>()
    private var talkButton: ImageView? = null
    private var exercise:String?=null
    private var workout:String?=null
    var receivedMessageNumber = 1
    var sentMessageNumber = 1
    var bpm: Float = 0.0f



    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)



        this.sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?;
        mHeartSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        sensorManager?.registerListener(
            this,
            mHeartSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )




        ListenerThread().start()



    }




    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.values!!.isNotEmpty()) {
            bpm = event.values[0]
            binding.bmpValue.text= "" + event.values[0].toInt()
            SendMessage("/my_path", bpm.toString()).start()


        }



    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged - accuracy: $accuracy");
    }

    override fun onPause() {
        sensorManager?.unregisterListener(this)
        super.onPause()
    }





    inner class Receiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            val intent1 = Intent(this@MainActivity, WorkoutPlayActivity::class.java)

            if (intent?.extras?.get("workout") != null) {
                workout= intent?.extras?.get("workout") as String

                intent1.putExtra("workout", workout)
                startActivity(intent1)


            }


        }
    }
    inner class ListenerThread() : Thread(){
        override fun run() {
            try {


                val newFilter = IntentFilter(Intent.ACTION_SEND)
                val messageReceiver = Receiver()
                LocalBroadcastManager.getInstance(this@MainActivity)
                    .registerReceiver(messageReceiver, newFilter);
            }
            catch (exception: ExecutionException) {

//TO DO//
            }
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