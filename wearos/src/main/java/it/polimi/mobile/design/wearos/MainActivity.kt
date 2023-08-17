package it.polimi.mobile.design.wearos
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log

import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.wearos.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import java.util.concurrent.ExecutionException

class MainActivity : Activity() , SensorEventListener {


    private var sensorManager: SensorManager? = null
    private var mHeartSensor: Sensor? = null

    private var workout:String?=null

    private var bpm: Float = 0.0f
    private val PERMISSION_REQUEST_CODE = 123



    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)



        this.sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?;
        mHeartSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BODY_SENSORS),
                PERMISSION_REQUEST_CODE
            )
        } else {
            registerHeartRateSensor()
        }
        sensorManager?.registerListener(
            this,
            mHeartSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        Log.d(TAG, "sensor initialized")





        ListenerThread().start()



    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerHeartRateSensor()
            } else {
                // L'utente ha negato il permesso, gestisci di conseguenza
            }
        }
    }




    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.values!!.isNotEmpty()) {
            bpm = event.values[0]
            binding.bmpValue.text= "" + event.values[0].toInt()
            SendMessage("/bpm", bpm.toString()).start()
            Log.d(TAG, "send bpm")


        }



    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged - accuracy: $accuracy");
    }

    override fun onPause() {
        sensorManager?.unregisterListener(this)
        super.onPause()
    }
    private fun registerHeartRateSensor() {
        mHeartSensor?.let {
            sensorManager?.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }





    inner class Receiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            val intent1 = Intent(this@MainActivity, WorkoutPlayActivity::class.java)

            if (intent?.extras?.get("workout") != null) {
                workout= intent?.extras?.get("workout") as String
                finish()
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


}