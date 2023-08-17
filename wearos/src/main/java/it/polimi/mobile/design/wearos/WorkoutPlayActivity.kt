package it.polimi.mobile.design.wearos

import android.annotation.SuppressLint
import android.content.*
import android.content.ContentValues.TAG
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.wearos.R

import com.example.wearos.databinding.ActivityWorkoutPlayBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.ExecutionException
import kotlin.properties.Delegates

class WorkoutPlayActivity : AppCompatActivity(), SensorEventListener{
    private var exerciseName: TextView? = null
    private var workoutName: TextView?= null
    private var sensorManager: SensorManager? = null
    private var mHeartSensor: Sensor? = null
    private lateinit var chrono: Chronometer
    private var timeWhenStopped by Delegates.notNull<Long>()
    private var talkButton: ImageView? = null
    private var exercise:String?=null
    var bpm: Float = 0.0f
    var i=0
    protected var myHandler: Handler? = null
    private lateinit var binding: ActivityWorkoutPlayBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityWorkoutPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("Play Workout", " onCreate play workout wearos"+ Thread.currentThread().id)

        talkButton=binding.startButton
        workoutName=binding.workoutName
        exerciseName=binding.exerciseName
        timeWhenStopped=0
        chrono=binding.workoutTimeValue
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val workoutValue =
            intent?.extras?.get("workout") as String
        workoutName?.text =workoutValue
        talkButton?.visibility= View.VISIBLE

        myHandler = Handler { msg ->
            val stuff = msg.data
            stuff.getString("messageText")?.let { messageText(it) }
            true
        }

        bpmThread().start()
        ListenerThread().start()
        SendMessage("/requestExercise", "r").start()
        beginWorkout()


        //Create an OnClickListener//
        talkButton!!.setOnClickListener {
            if (binding.startButton.drawable.constantState==resources.getDrawable(R.drawable.play).constantState) {





                SendMessage("/start", "start").start()

                binding.startButton.setImageResource(R.drawable.next)
                binding.exerciseName.text=exercise

            }
            else{
                SendMessage("/next", "next").start()
                chrono.stop()
                timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();
                binding.startButton.setImageResource(R.drawable.play)
                binding.exerciseName.text= "Rest, next:$exercise"
                chrono.base = SystemClock.elapsedRealtime()
                startChronometer()

            }

        }





    }
    private fun startCountdown(totalTimeMillis: Long, intervalMillis: Long, onTick: (Long) -> Unit, onFinish: () -> Unit) {
        val countDownTimer = object : CountDownTimer(totalTimeMillis, intervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                onFinish()
            }
        }

        countDownTimer.start()
    }

    private fun beginWorkout() {
        startCountdown(4999, 1, onTick = { millisUntilFinished ->
            setCountdownText(millisUntilFinished)
        }, onFinish = {
            startChronometer()
            binding.countDownCard.visibility = View.GONE
            binding.startButton.setImageResource(R.drawable.next)
        })
    }
    @SuppressLint("SetTextI18n")
    private fun setCountdownText(millisUntilFinished: Long) {
        val perc = (millisUntilFinished / 6000f) * 100
        binding.circularCountdownView.setProgress(perc.toInt())
        binding.countdown.text = ((millisUntilFinished / 1000).toInt() + 1).toString()
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.values!!.isNotEmpty()) {
            bpm = event.values[0]
            binding.bmpValue.text= "" + event.values[0].toInt()
            SendMessage("/bpm", bpm.toString()).start()
            Log.d(ContentValues.TAG, "bpm send to mobile device")



        }
    }
    fun messageText(newinfo: String) {
        if (newinfo.compareTo("") != 0) {

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(ContentValues.TAG, "onAccuracyChanged - accuracy: $accuracy");
    }





    @SuppressLint("SetTextI18n")
    private fun startChronometer(){
        chrono.visibility= View.VISIBLE
        chrono.onChronometerTickListener =
            Chronometer.OnChronometerTickListener { chronometer ->
                val time = SystemClock.elapsedRealtime() - chronometer.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                binding.hoursValue.text =  String.format("%02d", h) + "\u00A0"
                binding.minutesValue.text = String.format("%02d", m) + "\u00A0"
                binding.secondsValue.text = String.format("%02d", s) + "\u00A0"

            }
        chrono.base = SystemClock.elapsedRealtime()
        chrono.start()


    }







    inner class Receiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {


            if (intent?.extras?.get("workout")!=null){
                val onMessageReceived =
                    intent?.extras?.get("workout") as String
                if (onMessageReceived=="exit"){
                    onBackPressed()
                    finish()
                }

                else {
                    workoutName?.text =onMessageReceived


                    talkButton?.visibility  = View.VISIBLE
                }}
            if (intent?.extras?.get("exercise")!=null) {
                exercise =
                    intent?.extras?.get("exercise") as String
                binding.exerciseName.text=exercise
            }
            if (intent?.extras?.get("start")!=null) {

                startChronometer()
                binding.startButton.setImageResource(R.drawable.next)

            }
            if (intent?.extras?.get("next")!=null) {

                chrono.stop()

                binding.startButton.setImageResource(R.drawable.play)
                binding.exerciseName.text= "Rest, next:$exercise"
                chrono.base = SystemClock.elapsedRealtime()
                startChronometer()
            }
            if (intent?.extras?.get("finish")!=null) {
                chrono.stop()
                timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();
                finish()
                val intent1 = Intent(this@WorkoutPlayActivity, MainActivity::class.java)
                startActivity(intent1)
                finish()


            }
            if (intent?.extras?.get("stop")!=null) {
                chrono.stop()
                timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();
                binding.startButton.visibility=View.GONE
                binding.exerciseName.text = "FINISH!"



            }
            if (intent?.extras?.get("exit")!=null) {
                chrono.stop()
                timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();
                chrono.text="00:00:00"

                val intent1 = Intent(this@WorkoutPlayActivity, MainActivity::class.java)
                startActivity(intent1)
                finish()
            }
        }
    }
    inner class bpmThread : Thread(), SensorEventListener {
        override fun run() {
            try {
                this@WorkoutPlayActivity.sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
                mHeartSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
                sensorManager?.registerListener(
                    this,
                    mHeartSensor,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            } catch (exception: ExecutionException) {
                // exception
            }
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.values!!.isNotEmpty()) {
                val bpm = event.values[0].toInt()
                runOnUiThread {
                    binding.bmpValue.text = "$bpm"
                }
                SendMessage("/bpm", bpm.toString()).start()
                Log.d(ContentValues.TAG, "bpm send to mobile device")


            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d(ContentValues.TAG, "onAccuracyChanged - accuracy: $accuracy")
        }
    }
    fun sendMessage(messageText: String?) {
        val bundle = Bundle()
        bundle.putString("messageText", messageText)
        val msg: Message = myHandler!!.obtainMessage()
        msg.data = bundle
        myHandler!!.sendMessage(msg)
    }

    inner class ListenerThread() : Thread(){
        override fun run() {
            try {


                val newFilter = IntentFilter(Intent.ACTION_SEND)
                val messageReceiver = Receiver()
                LocalBroadcastManager.getInstance(this@WorkoutPlayActivity)
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
                    val sendMessageTask: Task<Int> = Wearable.getMessageClient(this@WorkoutPlayActivity)
                        .sendMessage(node.id, path, message.toByteArray())
                    try {
                        Log.d(ContentValues.TAG, "sending message to mobile device")
                        sendMessage(message)


//Handle the errors//
                    } catch (exception: ExecutionException) {
                        Log.e(TAG, "Exception thrown");

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






}