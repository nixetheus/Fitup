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
import android.os.Handler
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
import kotlin.properties.Delegates


class WorkoutPlayActivity : AppCompatActivity() {

    private val helperDB = DatabaseHelper().getInstance()
    private lateinit var binding : ActivityWorkoutPlayBinding
    private var mSpotifyAppRemote: SpotifyAppRemote? = null

    private lateinit var playWorkout: Workout
    private lateinit var workoutExercises: ArrayList<WorkoutExercise>
    private lateinit var wearableList : Task<List<Node>>

    private var currentExerciseIndex = 0
    private lateinit var globalElapsedTime: Chronometer
    private lateinit var exerciseElapsedTime: Chronometer
    private var timeWhenStopped by Delegates.notNull<Long>()

    private val handler = Handler()
    private var samplingBoolean = true
    private lateinit var bpmRunnable: Runnable
    private val bpmValues = mutableListOf<Float>()
    private val threads = mutableListOf<Thread>()
    private val messageReceiver = Receiver()




    override fun onCreate(savedInstanceState: Bundle?) {
        //currentExerciseIndex=0
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)





        retrieveData()

        initBpmTracking()
        beginWorkout()
        initChronometers()

        fillUITexts()


        createBindings()

        val newFilter = IntentFilter(Intent.ACTION_SEND)

        LocalBroadcastManager.getInstance(this@WorkoutPlayActivity)
            .registerReceiver(messageReceiver, newFilter)
    }
    private fun playPauseButtonClick(){
        binding.nextButton.visibility = View.VISIBLE
        binding.playPauseButton.visibility = View.INVISIBLE
        if (workoutExercises.isNotEmpty()) {
            if (currentExerciseIndex!=0){
                val newStartThread = SendThread("/start", "start")
                newStartThread.start()
                threads.add(newStartThread)
            }
            startExercise()
        } else {
            Toast.makeText(
                this, "Please add exercises to continue your training", Toast.LENGTH_SHORT
            ).show()
            Thread.sleep(500)
            val newFinishThread = SendThread("/finish", "finish")

            newFinishThread.start()
            threads.add(newFinishThread)
            val intent = Intent(this, EditWorkoutActivity::class.java)
            intent.putExtra("Workout", playWorkout)
            startActivity(intent)
            finish()
        }

    }

    private fun createBindings() {

        binding.playPauseButton.visibility = View.INVISIBLE
        binding.playPauseButton.setOnClickListener {
            playPauseButtonClick()
        }

        binding.nextButton.setOnClickListener {
            binding.nextButton.visibility = View.GONE
            binding.playPauseButton.visibility = View.VISIBLE
            if (workoutExercises.isNotEmpty()) {
                changeExercise()
            } else {
                Toast.makeText(
                    this, "Please add exercises to continue your training", Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.stopButton.setOnClickListener {
            finishWorkout()
        }

        binding.spotifyCard.setOnClickListener {
            startSpotify()
        }

    }

    private fun beginWorkout() {
        startCountdown(5000, 1, onTick = { millisUntilFinished ->
            setCountdownText(millisUntilFinished)
        }, onFinish = {
            startGlobalChronometer()
            binding.countDownCard.visibility = View.GONE
            playPauseButtonClick()
            handler.postDelayed(bpmRunnable, 10000)
        })
    }

    private fun initBpmTracking() {
        bpmRunnable = object : Runnable {
            override fun run() {
                samplingBoolean = true
                handler.postDelayed(this, 10000)
            }
        }
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
        timeWhenStopped=0
        globalElapsedTime = binding.workoutTimeValue
        exerciseElapsedTime = binding.exerciseCounter
    }

    private fun retrieveData() {
        wearableList = Wearable.getNodeClient(applicationContext).connectedNodes


        playWorkout = HelperFunctions().getSerializableExtra(intent, "Workout")!!



        helperDB.workoutsExercisesSchema.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists())
                    workoutExercises = helperDB.getWorkoutsExercisesFromSnapshot(snapshot, playWorkout.workoutId!!)

                if (workoutExercises.isNotEmpty()) {
                    playWorkout.name?.let {val newWorkoutThread = SendThread("/workout", it)
                        newWorkoutThread.start()
                        threads.add(newWorkoutThread)

                    }
                    binding.workoutLayoutPlay.populateExercises(workoutExercises)
                    setupExerciseUI()



                }
            else Log.d("workoutExercise from firebase ", "empty")}

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })


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
                        workoutData.totalNumberOfTimesPlayed = workoutData.totalNumberOfTimesPlayed?.plus(1)
                        helperDB.workoutsSchema.child(workoutData.workoutId!!).setValue(workoutData)
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

        centerChildViewInHorizontalScrollView (binding.playWorkoutScrollView)
    }
    
    private fun changeExercise() {
        binding.workoutLayoutPlay.completeExercise(currentExerciseIndex)
        if (currentExerciseIndex < workoutExercises.size - 1 ) {
            currentExerciseIndex++
            setupExerciseUI()
            Log.d("size before change exercise", "Exercise:"+workoutExercises.size.toString())

            val newNextThread=SendThread("/next", workoutExercises[currentExerciseIndex].exerciseName.toString())
            newNextThread.start()
            threads.add(newNextThread)
            Log.d("index of workoutExercises", currentExerciseIndex.toString())
        } else {
            showCongratulations()
            val newStopThread=SendThread("/stop", "stop")
            newStopThread.start()
            threads.add(newStopThread)
            Log.d("send stop from changeExercise, Thread:"+ Thread.currentThread().id.toString(), "empty workoutExercises")
            Log.d("index of workoutExercises", currentExerciseIndex.toString())


            binding.exerciseCounter.stop()

            binding.stopButton.visibility = View.VISIBLE
            binding.playPauseButton.visibility = View.INVISIBLE
            binding.nextButton.visibility = View.INVISIBLE
            timeWhenStopped = globalElapsedTime.base - SystemClock.elapsedRealtime();

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
        val newFinishThread=SendThread("/finish", "finish")
        newFinishThread.start()
        threads.add(newFinishThread)
        setWorkoutBPM()
        addUserExperience()
        setWorkoutNewPlaylist()
        disconnectSpotify()
        updateWorkoutStats()
        for(threads in threads){
            threads.interrupt()
        }
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

    private fun setWorkoutBPM() {
        val workoutRef = helperDB.workoutsSchema.orderByChild("workoutId").equalTo(playWorkout.workoutId!!)
        workoutRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (workoutSnapshot in dataSnapshot.children) {
                    val newValueBPM = (-playWorkout.averageBpmValue!! +
                            (playWorkout.averageBpmValue!! + bpmValues.average()) / 2).toInt()
                    workoutSnapshot.ref.child("averageBpmValue").setValue(
                        ServerValue.increment(newValueBPM.toLong()))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })

    }

    private fun setWorkoutNewPlaylist() {
        val workoutRef = helperDB.workoutsSchema.orderByChild("workoutId").equalTo(playWorkout.workoutId!!)
        workoutRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (workoutSnapshot in dataSnapshot.children) {
                    val newValueBPM = (-playWorkout.averageBpmValue!! +
                            (playWorkout.averageBpmValue!! + bpmValues.average()) / 2).toInt()
                    workoutSnapshot.ref.child("spotifyPlaylistLink").setValue(getSpotifyPlaylistByBPM(newValueBPM))
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
        intent.putExtra("bpm", bpmValues.average())
        finish()
        startActivity(intent)

        workoutExercises.clear()

    }

    fun getSpotifyPlaylistByBPM(bpm: Int): String {
        return when {
            bpm < 90  -> "spotify:playlist:1bS8K4F9XwIdYaaTM9Ljk6?si=d1ce6b04942d44ea"
            bpm < 100 -> "spotify:playlist:5JpANhLlGcgZcLFcrNhL7j?si=954d51a1b05b4ed1"
            bpm < 110 -> "spotify:playlist:2pX7htNxQUGZSObonznRyn?si=d27186a687ff4b20"
            bpm < 120 -> "spotify:playlist:2rzL3ZFSz87245ljAic93z?si=304dd1dda73e452c"
            bpm < 130 -> "spotify:playlist:2rzL3ZFSz87245ljAic93z?si=d50606cc7e7043e8"
            bpm < 140 -> "spotify:playlist:37i9dQZF1EIgOKtiospcqN?si=3152d927d92c4d64"
            bpm < 150 -> "spotify:playlist:37i9dQZF1EIgrZKdA44WQK?si=a15fd5fd6aec4230"
            bpm < 160 -> "spotify:playlist:37i9dQZF1DX0hWmn8d5pRe?si=4589da39252747dc"
            bpm < 170 -> "spotify:playlist:37i9dQZF1EIgfIackHptHl?si=b1c373a8d2644734"
            bpm < 180 -> "spotify:playlist:37i9dQZF1EIgUYhklBpeMG?si=36358560059b4fb0"
            else -> "spotify:playlist:37i9dQZF1EIcID9rq1OAoH?si=469b7b19873a47d9"
        }
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


                mSpotifyAppRemote!!.playerApi.play(playWorkout.averageBpmValue?.let {
                    playWorkout.spotifyPlaylistLink
                })
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
       /* playWorkout.name?.let {
            SendThread("/workout", it).start()
        }*/
    }

    // Inner Classes for WearOs communication

    // Start Thread to receive messages from WearOs
    inner class ListenerThread : Thread() {
        override fun run() {
            try {
                val newFilter = IntentFilter(Intent.ACTION_SEND)

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
            if (HelperFunctions().getExtra<String>(intent!!, "bpm")!=null) {
                val bpmValue = HelperFunctions().getExtra<String>(intent!!, "bpm")
                if (!bpmValue.isNullOrEmpty()) {
                    binding.bpmText.text = bpmValue
                    val bpmValueFloat = HelperFunctions().parseFloatInput(bpmValue!!)
                    if (bpmValueFloat > 0f) bpmValues.add(bpmValueFloat)
                }
            }
            
            // Start Exercise
            if (HelperFunctions().getExtra<String>(intent, "start") != null){ 
                binding.playPauseButton.performClick()
            }
            
            // Next Exercise
            if (HelperFunctions().getExtra<String>(intent, "next") != null) {
                binding.nextButton.visibility = View.INVISIBLE
                binding.playPauseButton.visibility = View.VISIBLE
                Log.d("size before change exercise", "Exercise:"+workoutExercises.size.toString())

                binding.nextButton.performClick()
                Log.d("size after change exercise", "Exercise:"+workoutExercises.size.toString())


            }
            
            // Info Request
            if (HelperFunctions().getExtra<String>(intent, "requestExercise") != null) {
                if (workoutExercises.isNotEmpty()) {
                    val newExerciseThread =
                        SendThread("/exercise", workoutExercises[currentExerciseIndex].exerciseName.toString())

                    newExerciseThread.start()
                    threads.add(newExerciseThread)
                    Log.d("send exercise from requestExercise", "Exercise:"+workoutExercises[currentExerciseIndex].exerciseName.toString())
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
        val newFinishThread=SendThread("/finish", "finish")
        newFinishThread.start()
        threads.add(newFinishThread)
        disconnectSpotify()
    }

    // TODO: modernize
    override fun onBackPressed() {
        super.onBackPressed()
        val newFinishThread=SendThread("/finish", "finish")
        newFinishThread.start()
        threads.add(newFinishThread)
        disconnectSpotify()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
        workoutExercises.clear()
        finishAffinity()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
        super.onDestroy()
        ListenerThread().interrupt()
        disconnectSpotify()



        handler.removeCallbacks(bpmRunnable)
        handler.removeCallbacksAndMessages(null)
        val newFinishThread=SendThread("/finish", "finish")
        newFinishThread.start()
        threads.add(newFinishThread)
        workoutExercises.clear()
        for (threads in threads) {
            threads.interrupt()
        }
        finish()

    }
    override fun onResume() {
        super.onResume()
        helperDB.workoutsExercisesSchema.keepSynced(true)
    }
    override fun onPause() {
        super.onPause()
        helperDB.workoutsExercisesSchema.keepSynced(false)
    }
}

