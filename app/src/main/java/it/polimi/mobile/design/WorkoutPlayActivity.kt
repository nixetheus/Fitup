package it.polimi.mobile.design

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import it.polimi.mobile.design.databinding.ActivityWorkoutPlayBinding
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.entities.WorkoutUserData
import it.polimi.mobile.design.helpers.Constant
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions
import java.util.concurrent.ExecutionException


class WorkoutPlayActivity : AppCompatActivity() {

    private val helperDB = DatabaseHelper().getInstance()
    private lateinit var binding : ActivityWorkoutPlayBinding
    private var mSpotifyAppRemote: SpotifyAppRemote? = null

    private lateinit var playWorkout: Workout
    private lateinit var workoutExercises: ArrayList<WorkoutExercise>
    private lateinit var wearableList : Task<List<Node>>
    
    private var currentExerciseIndex = -1
    private lateinit var globalElapsedTime: Chronometer
    private lateinit var exerciseElapsedTime: Chronometer


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        beginWorkout()
        initChronometers()
        retrieveData()

        fillUITexts()
        updateWorkoutStats()

        createBindings()
        startSpotify()
        sendWorkoutNameToWearable()
        ListenerThread().start()
    }

    private fun createBindings() {

        binding.playPauseButton.visibility = View.INVISIBLE
        binding.playPauseButton.setOnClickListener {
            binding.nextButton.visibility = View.VISIBLE
            binding.playPauseButton.visibility = View.INVISIBLE
            if (workoutExercises.isNotEmpty()) {
                startExercise()
            } else {
                Toast.makeText(
                    this, "Please add exercises to continue your training", Toast.LENGTH_SHORT).show()
            }
        }

        binding.nextButton.setOnClickListener {
            binding.nextButton.visibility = View.INVISIBLE
            binding.playPauseButton.visibility = View.VISIBLE
            if (workoutExercises.isNotEmpty()) {
                changeExercise()
            } else {
                Toast.makeText(
                    this, "Please add exercises to continue your training", Toast.LENGTH_SHORT).show()
            }
        }

        binding.stopButton.setOnClickListener {
            finishWorkout()
        }
    }

    private fun beginWorkout() {
        startCountdown(5000, 1, onTick = { millisUntilFinished ->
            setCountdownText(millisUntilFinished)
        }, onFinish = {
            startGlobalChronometer()
            binding.countDownCard.visibility = View.GONE
            binding.playPauseButton.performClick()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setCountdownText(millisUntilFinished: Long) {
        val perc = (millisUntilFinished / 6000f) * 100
        binding.circularCountdownView.setProgress(perc.toInt())
        binding.countdown.text = ((millisUntilFinished / 1000).toInt() + 1).toString()
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

    private fun initChronometers() {
        globalElapsedTime = binding.workoutTimeValue
        exerciseElapsedTime = binding.exerciseCounter
    }

    private fun retrieveData() {

        playWorkout = HelperFunctions().getSerializableExtra(intent, "Workout")!!

        helperDB.workoutsExercisesSchema.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists())
                    workoutExercises = helperDB.getWorkoutsExercisesFromSnapshot(snapshot, playWorkout.workoutId!!)

                if (workoutExercises.isNotEmpty()) {
                    binding.workoutLayoutPlay.populateExercises(workoutExercises)
                    changeExercise()
                    SendThread("/exercise", workoutExercises[currentExerciseIndex].exerciseName.toString()).start()
                }}

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })

        wearableList = Wearable.getNodeClient(applicationContext).connectedNodes
    }

    private fun startGlobalChronometer() {
        globalElapsedTime.onChronometerTickListener =
            OnChronometerTickListener { chronometer ->
                chronometer.text = formatTime(SystemClock.elapsedRealtime() - chronometer.base, "HH:mm:ss")
            }
        globalElapsedTime.base = SystemClock.elapsedRealtime()
        globalElapsedTime.start()
    }

    private fun startChronometerExercise() {
        exerciseElapsedTime.onChronometerTickListener =
            OnChronometerTickListener { chronometer ->
                chronometer.text = formatTime(SystemClock.elapsedRealtime() - chronometer.base, "mm:ss")
            }
        exerciseElapsedTime.base = SystemClock.elapsedRealtime()
        exerciseElapsedTime.start()
    }

    private fun updateWorkoutStats() {
        updateWorkoutUserData()
        updateWorkoutTotalPlays()
    }

    private fun updateWorkoutUserData() {
        helperDB.workoutUserDataSchema.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    val workoutUserData = helperDB.getUserWorkoutDataFromSnapshot(dataSnapshot)
                    val playWorkoutData = workoutUserData.find { it.workoutId == playWorkout.workoutId!! }

                    val lastDate        = System.currentTimeMillis()
                    val timesPlayed     = playWorkoutData?.numberOfTimesPlayed?.plus(1) ?: 1

                    val uid   = helperDB.getFirebaseAuth().uid
                    val wid   = playWorkout.workoutId!!
                    val wUDid = playWorkoutData?.id ?: helperDB.workoutUserDataSchema.push().key!!

                    val newWorkoutUserData = WorkoutUserData(wUDid, uid, wid, lastDate, timesPlayed)
                    helperDB.workoutUserDataSchema.child(wUDid).setValue(newWorkoutUserData)

                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateWorkoutTotalPlays() {
        helperDB.workoutsSchema.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userWorkouts = helperDB.getWorkoutsFromSnapshot(dataSnapshot)
                    val workoutData = userWorkouts.find { it.workoutId == playWorkout.workoutId!! }
                    if (workoutData != null) {
                        workoutData.totalNumberOfTimesPlayed?.plus(1)
                        helperDB.workoutUserDataSchema.child(playWorkout.workoutId!!).setValue(workoutData)
                    } else {
                        throw java.lang.IllegalStateException()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    
    private fun fillUITexts() {
        binding.expText.text          = "0"
        binding.playWorkoutName.text  = playWorkout.name
        binding.exerciseCounter.text  = resources.getString(R.string.exercise_time)
        binding.workoutTimeValue.text = resources.getString(R.string.global_time)
    }
    
    private fun startExercise() {
        startChronometerExercise()
        SendThread("/start", "start").start()
        centerChildViewInHorizontalScrollView (binding.playWorkoutScrollView)
    }
    
    private fun changeExercise() {
        binding.workoutLayoutPlay.completeExercise(currentExerciseIndex)
        if (currentExerciseIndex < workoutExercises.size - 1 ) {
            currentExerciseIndex++
            setupExerciseUI()
            SendThread("/next", "next").start()
        } else {
            showCongratulations()
            binding.exerciseCounter.stop()
            binding.workoutTimeValue.stop()
            binding.stopButton.visibility = View.VISIBLE
            binding.playPauseButton.visibility = View.INVISIBLE
            binding.nextButton.visibility = View.INVISIBLE

            val colorAccent = TypedValue()
            this.theme.resolveAttribute(android.R.attr.colorAccent, colorAccent, true)
            binding.endInside.setCardBackgroundColor(colorAccent.data)
            setHorizontalScrollViewEnd(binding.playWorkoutScrollView)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showCongratulations() {
        binding.courageText.text = resources.getString(R.string.congrats)
        binding.endImage.setImageDrawable(
            resources.getDrawable(R.drawable.monster_defeated, applicationContext.theme)!!)
    }

    private fun centerChildViewInHorizontalScrollView(horizontalScrollView: HorizontalScrollView) {
        val startingOffset = 70.toPx()
        val minimizedExerciseViewOffset = 150.toPx()
        horizontalScrollView.post {
            // Calculate the horizontal offset needed to center the child view in the HorizontalScrollView
            val scrollOffset = startingOffset + (currentExerciseIndex) * minimizedExerciseViewOffset
            // Set the calculated offset as the new scroll position of the HorizontalScrollView
            horizontalScrollView.smoothScrollTo(scrollOffset, 0)
        }
    }

    private fun setHorizontalScrollViewEnd(horizontalScrollView: HorizontalScrollView) {
        horizontalScrollView.post {
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }
    }

    private fun setupExerciseUI() {
        with (binding) {
            currentExerciseName.text = workoutExercises[currentExerciseIndex].exerciseName
            currentExerciseRepsValue.text = workoutExercises[currentExerciseIndex].reps.toString()
            currentExerciseSetsValue.text = workoutExercises[currentExerciseIndex].sets.toString()
            currentExerciseWeightValue.text = workoutExercises[currentExerciseIndex].weight.toString()
            currentExerciseRestValue.text = HelperFunctions().secondsToFormatString(workoutExercises[currentExerciseIndex].rest!!)
            startCurrentExerciseLayout.visibility = View.VISIBLE
        }
        exerciseElapsedTime.stop()
        exerciseElapsedTime.text = resources.getString(R.string.exercise_time)
    }

    private fun finishWorkout() {
        SendThread("/finish", "finish").start()
        addUserExperience()
        disconnectSpotify()
        startFinishActivity()
    }

    private fun addUserExperience() {
        val userRef = helperDB.usersSchema.orderByChild("uid").equalTo(helperDB.getFirebaseAuth().uid)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    userSnapshot.ref.child("exp").setValue(
                        ServerValue.increment(playWorkout.gainedExperience!!.toDouble()))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    private fun disconnectSpotify() {
        mSpotifyAppRemote?.playerApi?.pause()
        mSpotifyAppRemote?.let { SpotifyAppRemote.disconnect(it) }
    }

    private fun startFinishActivity() {
        val intent = Intent(this, WorkoutEndActivity::class.java)
        intent.putExtra("Exp", playWorkout.gainedExperience)
        intent.putExtra("Time", globalElapsedTime.base)
        intent.putExtra("N", playWorkout.numberOfExercises)
        startActivity(intent)
        finish()
    }

    private fun startSpotify() {
        
        val connectionParams = ConnectionParams.Builder(Constant.CLIENT_ID)
            .setRedirectUri(Constant.REDIRECT_URI)
            .showAuthView(true)
            .build()
        
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
                
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote
                Log.d("Play Workout", "Connected! Yay!")
                mSpotifyAppRemote!!.playerApi.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL")
                spotifyAppRemote.playerApi.subscribeToPlayerState().setEventCallback {
                    val track: Track = it.track
                    Log.d("PlaystartSpotify() Workout", track.name + " by " + track.artist.name)
                }
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
            }
        })
    }

    private fun sendWorkoutNameToWearable() {
        playWorkout.name?.let {
            SendThread("/workout", it).start()
        }
    }

    // Inner Classes for WearOs communication

    // Start Thread to receive messages from WearOs
    inner class ListenerThread : Thread() {
        override fun run() {
            try {
                val newFilter = IntentFilter(Intent.ACTION_SEND)
                val messageReceiver = Receiver()
                LocalBroadcastManager.getInstance(this@WorkoutPlayActivity)
                    .registerReceiver(messageReceiver, newFilter)
            }
            catch (_: ExecutionException) {}
        }
    }
    
    // Receive messages from WearOs Device
    inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            
            // Update BPM
            val bpmValue = HelperFunctions().getExtra<String>(intent!!, "message")
            if (bpmValue.isNullOrEmpty()) { binding.bpmText.text = bpmValue }
            
            // Start Exercise
            if (HelperFunctions().getExtra<String>(intent, "start") != null){ 
                binding.playPauseButton.performClick()
            }
            
            // Next Exercise
            if (HelperFunctions().getExtra<String>(intent, "next") != null) {
                binding.nextExerciseButton.performClick()
            }
            
            // Info Request
            if (HelperFunctions().getExtra<String>(intent, "request") != null) {
                if (workoutExercises.isNotEmpty()) {
                    SendThread("/exercise", workoutExercises[currentExerciseIndex].exerciseName.toString()).start()
                }
            }
        }
    }

    // Send data to WearOs Device
    inner class SendThread (var path: String, private var message: String) : Thread() {
        override fun run() {
            try {
                val nodes: List<Node> = Tasks.await(wearableList)
                for (node in nodes) {
                    // val sendMessageTask: Task<Int> = Save into variable if result needed
                    Wearable.getMessageClient(this@WorkoutPlayActivity).sendMessage(node.id, path, message.toByteArray())
                }
                Log.d(TAG, "nodes: ${nodes.size}")
            }
            catch (_: ExecutionException) {}
            catch (_: InterruptedException) {}
        }
    }

    // Helpers
    private fun formatTime(time: Long, format: String): String {
        val h = time / 3600000
        val m = (time % 3600000) / 60000
        val s = (time % 60000) / 1000

        return when (format) {
            "HH:mm:ss" -> String.format("%02d:%02d:%02d", h, m, s)
            "mm:ss" -> String.format("%02d:%02d", m, s)
            else -> throw IllegalArgumentException("Invalid format: $format")
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
    
    // Others
    override fun onStop() {
        super.onStop()
        SendThread("/exit", "exit").start()
    }

    // TODO: modernize
    override fun onBackPressed() {
        super.onBackPressed()
        SendThread("/exit", "exit").start()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        SendThread("/exit", "exit").start()
    }
}

