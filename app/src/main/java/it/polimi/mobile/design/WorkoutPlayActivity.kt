package it.polimi.mobile.design

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import kotlin.properties.Delegates


class WorkoutPlayActivity : AppCompatActivity() {
    private val FORMAT = "%02d:%02d"
    private lateinit var binding:ActivityWorkoutPlayBinding
    private lateinit var chrono: Chronometer
    private lateinit var database: DatabaseReference
    private var db = FirebaseDatabase.getInstance()
    private lateinit var workoutExercise: ArrayList<WorkoutExercise>
    private var timeWhenStopped by Delegates.notNull<Long>()
    private var i=0
    private val CLIENT_ID = "8db4d298653041b4b1850c09464182a3"
    private val REDIRECT_URI = "mobile-app-login://callback"
    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userDatabase = FirebaseDatabase.getInstance().getReference("Users")
    private val databaseHelperInstance = DatabaseHelper().getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        val experience =0F
        super.onCreate(savedInstanceState)
        binding=ActivityWorkoutPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        workoutExercise= arrayListOf<WorkoutExercise>()
        timeWhenStopped=0
        chrono= binding.workoutTimeValue
        chrono.text = "00:00:00"

        var workout= intent.extras?.get("workout") as Workout
        var exp= intent.extras?.get("exp") as Float
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

                                    workoutExercise.add(workData!!)
                                }
                            }
                        }
                    }


                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        binding.beingTimeButton.setOnClickListener{

            start()

            binding.nextExerciseButton.text="next"
            binding.beingTimeButton.text=""
            binding.beingTimeButton.isClickable=false
            binding.nextExerciseButton.isClickable=true

        }

        binding.startStopButton.setOnClickListener{
            i=0
            binding.nextExerciseButton.text=""
            binding.nextExerciseButton.isClickable=false

            if (workoutExercise.size!=0) {
                binding.currentExerciseName.text = workoutExercise[i].exerciseName
                binding.currentExerciseRepsValue.text=workoutExercise[i].reps.toString()
                binding.currentExerciseRestValue.text=workoutExercise[i].rest.let {
                    it?.let { it1 -> HelperFunctions().secondsToFormatString(it1.toInt()) }
                }
                binding.currentExerciseSetsValue.text=workoutExercise[i].sets.toString()
                binding.startCurrentExerciseLayout.visibility= View.VISIBLE
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

                            mSpotifyAppRemote!!.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
                            spotifyAppRemote.playerApi.subscribeToPlayerState().setEventCallback {
                                val track: Track = it.track
                                Log.d("Play Workout", track.name + " by " + track.artist.name)
                            }
                        }

                        override fun onFailure(throwable: Throwable) {
                            Log.e("MainActivity", throwable.message, throwable)



                            // Something went wrong when attempting to connect! Handle errors here
                        }

                    })
            }
            else {
                Toast.makeText(
                    this,
                    "This workout is empty, please add exercises to continue your training",
                    Toast.LENGTH_SHORT
                ).show()
                chrono.stop()
                binding.startStopButton.text = "FINISH!!"
                binding.startStopButton.setOnClickListener {
                    val intent = Intent(this, CentralActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.nextExerciseButton.setOnClickListener{
            chrono.stop()
            timeWhenStopped = chrono.base - SystemClock.elapsedRealtime();
            binding.nextExerciseButton.text=""
            binding.nextExerciseButton.isClickable=false
            binding.beingTimeButton.text="begin"
            binding.beingTimeButton.isClickable=true
            if(i<workoutExercise.size-1){
                i++
                binding.currentExerciseName.text = workoutExercise[i].exerciseName
                binding.currentExerciseRepsValue.text=workoutExercise[i].reps.toString()
                binding.currentExerciseRestValue.text=workoutExercise[i].rest.let {
                    it?.let { it1 -> HelperFunctions().secondsToFormatString(it1.toInt()) }
                }
                binding.currentExerciseSetsValue.text=workoutExercise[i].sets.toString()
            }
            else {

                binding.startStopButton.text = "FINISH!!"

                binding.startCurrentExerciseLayout.visibility = View.GONE
                binding.startStopButton.setOnClickListener {

                    val workoutToRemove =
                        db.reference.child("Workout").orderByChild("workoutId").equalTo(workout.workoutId)

                    workoutToRemove.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (workoutSnapshot in dataSnapshot.children) {
                                workoutSnapshot.ref.child("ranking").setValue(ServerValue.increment(-1))
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                        }
                    })

                    //userDatabase.child(firebaseAuth.uid.toString()).child("exp").setValue(workout..toInt())
                    mSpotifyAppRemote?.playerApi?.pause()
                    mSpotifyAppRemote?.let {

                        SpotifyAppRemote.disconnect(it)
                    }

                    val intent = Intent(this, WorkoutEndActivity::class.java)
                    intent.putExtra("exp", exp)
                    intent.putExtra("time", chrono.base)
                    intent.putExtra("number of exercises", i)
                    startActivity(intent)
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()
        var workout= intent.extras?.get("workout") as Workout


    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
        true
    }




    private fun start(){
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


}