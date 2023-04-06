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
import android.widget.Button
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

    var receivedMessageNumber = 1

    var sentMessageNumber = 1
    var bpm: Float = 0.0f


    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        talkButton=binding.startButton
        workoutName=binding.workoutName
        exerciseName=binding.exerciseName
        timeWhenStopped=0
        chrono=binding.workoutTimeValue
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)



        this.sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?;
        mHeartSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        sensorManager?.registerListener(
            this,
            mHeartSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )




        //Create an OnClickListener//
        talkButton!!.setOnClickListener {
            if (binding.startButton.drawable.constantState==resources.getDrawable(R.drawable.play).constantState) {
                SendMessage("/start", "start").start()
                binding.startButton.setImageResource(R.drawable.next)
                binding.exerciseName.text=exercise
                startChronometer()
            }
            else{
                SendMessage("/next", "next").start()
                chrono.stop()
                timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();
                binding.startButton.setImageResource(R.drawable.play)
                binding.exerciseName.text= "next exercise$exercise"

            }
            //talkButton!!.text="next"}
           // if (talkButton!!.text=="next"){
            //    SendMessage("/my_path", "next").start()
            //}
        }
        val newFilter = IntentFilter(Intent.ACTION_SEND)

        val messageReceiver = Receiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);



    }




    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.values!!.isNotEmpty()) {
            bpm = event.values[0]
            binding.bmpValue.text= "" + event.values[0].toInt()
            SendMessage("/my_path", bpm.toString()).start()
            //SendMessage("/ciao", bpm.toString()).start()


        }



    }
    private fun startChronometer(){
        chrono.onChronometerTickListener =
            Chronometer.OnChronometerTickListener { chronometer ->
                val time = SystemClock.elapsedRealtime() - chronometer.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                val milli = (time - h * 3600000 - m * 60000 - s * 1000).toInt()
                val t =
                    (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
                chronometer.text = "" // t

                // UI TESTING TODO
                binding.hoursValue.text =  String.format("%02d", h) + "\u00A0"
                binding.minutesValue.text = String.format("%02d", m) + "\u00A0"
                binding.secondsValue.text = String.format("%02d", s) + "\u00A0"

            }
        chrono.base = SystemClock.elapsedRealtime() + timeWhenStopped
        chrono.start()


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

//Display the following when a new message is received//
            if (intent?.extras?.get("workout")!=null){
            val onMessageReceived =
                intent?.extras?.get("workout") as String
                if (onMessageReceived=="exit"){
                    onBackPressed()
                }

                else {
                    workoutName?.text = "workout: $onMessageReceived"


                    talkButton?.visibility  = View.VISIBLE
                }}
            if (intent?.extras?.get("exercise")!=null) {
                exercise =
                    intent?.extras?.get("exercise") as String
                binding.exerciseName.text= "next exercise: $exercise"
            }
            if (intent?.extras?.get("start")!=null) {

                startChronometer()
                binding.exerciseName.text=exercise
                binding.startButton.setImageResource(R.drawable.next)
                binding.exerciseName.text= exercise
            }
            if (intent?.extras?.get("next")!=null) {

                chrono.stop()
                timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();
                binding.startButton.setImageResource(R.drawable.play)
            }
            if (intent?.extras?.get("finish")!=null) {
                chrono.stop()
                timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();
                val intent1 = Intent(this@MainActivity, EndWorkoutActivity::class.java)
                startActivity(intent1)
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