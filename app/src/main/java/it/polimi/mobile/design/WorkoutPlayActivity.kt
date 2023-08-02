package it.polimi.mobile.design

import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import it.polimi.mobile.design.databinding.ActivityWorkoutPlayBinding
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions
import java.util.concurrent.ExecutionException
import kotlin.properties.Delegates


class WorkoutPlayActivity : AppCompatActivity() {
    private val FORMAT = "%02d:%02d"
    private lateinit var binding:ActivityWorkoutPlayBinding
    private lateinit var chrono: Chronometer
    private lateinit var chronoExercise: Chronometer
    private lateinit var database: DatabaseReference
    private var db = FirebaseDatabase.getInstance()
    private lateinit var workoutExercise: ArrayList<WorkoutExercise>
    private lateinit var workout: Workout
    private var timeWhenStopped by Delegates.notNull<Long>()
    private var i=0
    private lateinit var mGoogleApiClient:GoogleApiClient
    private val CLIENT_ID = "8db4d298653041b4b1850c09464182a3"
    private val REDIRECT_URI = "mobile-app-login://callback"
    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userDatabase = FirebaseDatabase.getInstance().getReference("Users")
    private val databaseHelperInstance = DatabaseHelper().getInstance()
    private var notificationId:Int=0;
    private var exp by Delegates.notNull<Float>()
    protected var myHandler: Handler? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWorkoutPlayBinding.inflate(layoutInflater)
        Log.d("Play Workout", "onCreate workout play mobile")
        setContentView(binding.root)
        i=0

        workoutExercise= arrayListOf<WorkoutExercise>()
        workoutExercise.clear()
        timeWhenStopped=0
        chrono= binding.workoutTimeValue
        chronoExercise=binding.exerciseCounter
        chronoExercise.text = "00:00"
        chrono.text = "00:00:00"
        myHandler = Handler { msg ->
            val stuff = msg.data
            stuff.getString("messageText")?.let { messageText(it) }
            true
        }
        startSpotify()

        // TODO: add to workoutUserData
        // TODO: add to total workout data

        workout= intent.extras?.get("Workout") as Workout
        exp= 0f

        binding.expText.text=exp.toString()
        binding.playWorkoutName.text=workout.name
        database=FirebaseDatabase.getInstance().getReference("WorkoutExercise")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutExercise.clear()
                if (snapshot.exists()){
                    for (workSnap in snapshot.children) {
                        val workData = workSnap.getValue(WorkoutExercise::class.java)
                        if (workData != null) {
                            if (workData.workoutId == workout.workoutId) {

                                    workoutExercise.add(workData)
                                }
                            }
                        }
                    }

                    binding.workoutLayoutPlay.exercises = workoutExercise
                if( workoutExercise.size!=0) {
                NewThread("/exercise", workoutExercise[i].exerciseName.toString()).start()
                }}

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        sendWorkoutNameToWearable()
        ListenerThread().start()






        binding.playPauseButton.setOnClickListener{
            if (i==0)
                startChronometer()
            playExercise()
            NewThread("/start", "start").start()
        }
        binding.nextButton.setOnClickListener{
            NewThread("/next", "next").start()
            nextExercise(exp)

        }
        binding.wearOsButton.setOnClickListener{



        }
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

    private fun playExercise() {

        if (workoutExercise.size!=0) {
            binding.currentExerciseName.text = workoutExercise[i].exerciseName
            binding.currentExerciseRepsValue.text=workoutExercise[i].reps.toString()
            binding.currentExerciseRestValue.text=workoutExercise[i].rest.let {
                it?.let { it1 -> HelperFunctions().secondsToFormatString(it1.toInt()) }
            }
            binding.currentExerciseSetsValue.text=workoutExercise[i].sets.toString()
            binding.startCurrentExerciseLayout.visibility= View.VISIBLE

            startChronometerExercise()


            binding.playPauseButton.isClickable=false
            binding.nextButton.isClickable=true




        }
        else {
            Toast.makeText(
                this,
                "This workout is empty, please add exercises to continue your training",
                Toast.LENGTH_SHORT
            ).show()
            chrono.stop()
            chronoExercise.stop()
            chronoExercise.text="00:00"
            binding.startStopButton.text = "FINISH!!"
            binding.startStopButton.setOnClickListener {
                val intent = Intent(this, CentralActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun nextExercise(exp:Float){
        //chrono.stop()
        chronoExercise.stop()
        chronoExercise.text="00:00"
        //timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();

        binding.nextButton.isClickable=false

        binding.playPauseButton.isClickable=true
        if(i<workoutExercise.size-1){
            i++
            binding.currentExerciseName.text = workoutExercise[i].exerciseName
            binding.currentExerciseRepsValue.text=workoutExercise[i].reps.toString()
            binding.currentExerciseRestValue.text=workoutExercise[i].rest.let {
                it?.let { it1 -> HelperFunctions().secondsToFormatString(it1.toInt()) }
            }
            binding.currentExerciseSetsValue.text=workoutExercise[i].sets.toString()
            NewThread("/exercise", workoutExercise[i].exerciseName.toString()).start()

        }
        else {

            NewThread("/finish", "finish").start()


            binding.startCurrentExerciseLayout.visibility = View.GONE


                val workoutToRemove =
                    db.reference.child("Workout").orderByChild("workoutId").equalTo(workout.workoutId)

                workoutToRemove.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (workoutSnapshot in dataSnapshot.children) {
                            workoutSnapshot.ref.child("ranking").setValue(ServerValue.increment(-1))

                            workoutSnapshot.ref.child("timestamp").setValue(ServerValue.TIMESTAMP)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                    }
                })
            val userExp=db.reference.child("Users").orderByChild("uid").equalTo(firebaseAuth.uid)
                userExp.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (userSnapshot in dataSnapshot.children) {
                            userSnapshot.ref.child("exp").setValue(ServerValue.increment(exp.toDouble()))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                    }

                })


                mSpotifyAppRemote?.playerApi?.pause()
                mSpotifyAppRemote?.let {

                    SpotifyAppRemote.disconnect(it)
                }

                val intent = Intent(this, WorkoutEndActivity::class.java)
                intent.putExtra("exp", exp)
                intent.putExtra("time", chrono.base)
                intent.putExtra("number of exercises", i)
            workoutExercise.clear()
                startActivity(intent)
            finish()
                i=0

        }
    }
    fun startSpotify(){
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    Log.d("Play Workout", "Connected! Yay!")

                    // Now you can start interacting with App Remote

                    mSpotifyAppRemote!!.playerApi.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
                    spotifyAppRemote.playerApi.subscribeToPlayerState().setEventCallback {
                        val track: Track = it.track
                        Log.d("PlaystartSpotify() Workout", track.name + " by " + track.artist.name)
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("MainActivity", throwable.message, throwable)



                    // Something went wrong when attempting to connect! Handle errors here
                }

            })
    }

    override fun onStart() {
        super.onStart()
        i=0


    }





    private fun startChronometer(){
        chrono.onChronometerTickListener =
            OnChronometerTickListener { chronometer ->
                val time = SystemClock.elapsedRealtime() - chronometer.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                val t =
                    (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
                chronometer.text = t
            }
        chrono.base = SystemClock.elapsedRealtime() + timeWhenStopped;
        chrono.start()


    }
    private fun startChronometerExercise(){
        chronoExercise.onChronometerTickListener =
            OnChronometerTickListener { chronometer ->
                val time = SystemClock.elapsedRealtime() - chronometer.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                val t =
                    (if (m < 10) "0$m" else m.toString()) + ":" + if (s < 10) "0$s" else s
                chronometer.text = t
            }
        chronoExercise.base = SystemClock.elapsedRealtime()
        chronoExercise.start()


    }

    override fun onStop() {
        super.onStop()
        NewThread("/exit", "exit").start()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
        NewThread("/exit", "exit").start()
    }

    override fun onDestroy() {
        super.onDestroy()
        NewThread("/exit", "exit").start()
    }

    fun messageText(newinfo: String) {
        if (newinfo.compareTo("") != 0) {
           /* binding.currentExerciseName
                .append(
                """
                
                $newinfo
                """.trimIndent()
            )*/
        }
    }


    //Define a nested class that extends BroadcastReceiver//
     inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.extras?.get("message") != null) {

                val message = intent?.extras?.get("message") as String

                binding.bpmText.text = message + " bpm"
            }
            if (intent?.extras?.get("start")!=null){
                if (workoutExercise.size!=0){
                    if(i==0)
                        startChronometer()
                    playExercise()
                    startExerciseAnimation()
                    Log.d("Play Workout", "Exercise"+i+"of"+workoutExercise.size+":"+workoutExercise[i].exerciseName)

                }



            }
            if (intent?.extras?.get("next")!=null) {
                if (workoutExercise.size != 0) {

                    Log.d("Next Exercise", "nextExercise"+i+"of"+workoutExercise.size+":"+workoutExercise[i].exerciseName+ Thread.currentThread().id.toString())

                    nextExercise(exp)

                }
            }
            if (intent?.extras?.get("request")!=null) {
                if (workoutExercise.size != 0) {
                    NewThread("/exercise", workoutExercise[i].exerciseName.toString()).start()

                }
            }

        }
    }
    fun startExerciseAnimation(){
        TODO()
    }

    fun sendWorkoutNameToWearable() {

        workout.name?.let { NewThread("/workout", it).start() }
       // workoutExercise[0].exerciseName?.let { NewThread("/exercise", it).start() }

    }

    //Use a Bundle to encapsulate our message//
    fun sendMessage(messageText: String?) {
        val bundle = Bundle()
        bundle.putString("messageText", messageText)
        val msg: Message = myHandler!!.obtainMessage()
        msg.data = bundle
        myHandler!!.sendMessage(msg)
    }


   inner class NewThread     //Constructor for sending information to the Data Layer//
        (var path: String, var message: String) : Thread() {
        override fun run() {

//Retrieve the connected devices, known as nodes//
            val wearableList: Task<List<Node>> =
                Wearable.getNodeClient(applicationContext).connectedNodes
            try {
                val nodes: List<Node> = Tasks.await<List<Node>>(wearableList)
                for (node in nodes) {
                    val sendMessageTask: Task<Int> =  //Send the message//
                        Wearable.getMessageClient(this@WorkoutPlayActivity)
                            .sendMessage(node.id, path, message.toByteArray())
                    try {

//Block on a task and get the result synchronously//
                       val result = Tasks.await<Int>(sendMessageTask)
                        Log.d(TAG, "Data item set: $path")



                        //if the Task fails, then…..//
                    } catch (exception: ExecutionException) {

                        //TO DO: Handle the exception//
                    } catch (exception: InterruptedException) {

                        //TO DO: Handle the exception//
                    }
                }
                Log.d(TAG, "nodes: ${nodes.size}")
            } catch (exception: ExecutionException) {

                //TO DO: Handle the exception//
            } catch (exception: InterruptedException) {

                //TO DO: Handle the exception//
            }
        }
    }

}

