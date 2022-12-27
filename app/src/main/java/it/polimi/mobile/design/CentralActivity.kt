package it.polimi.mobile.design


import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import android.widget.Toolbar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.entities.Workout


class CentralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCentralBinding
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var workoutArrayList:ArrayList<Workout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCentralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workoutArrayList= arrayListOf<Workout>()
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.uid.toString()
        database= FirebaseDatabase.getInstance().getReference("Workout")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutArrayList.clear()
                if (snapshot.exists()) {
                    for (workSnap in snapshot.children) {
                        val workData = workSnap.getValue(Workout::class.java)
                        if (workData != null) {
                            if (workData.userId==uid)
                                workoutArrayList.add(workData!!)
                        }

                    }
                    showTopWorkouts(workoutArrayList)
                    showPopularWorkouts(workoutArrayList)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        showUser()


        binding.welcomeView.setOnClickListener{
            firebaseAuth.signOut()
            LoginManager.getInstance().logOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        
        binding.exercisesLink.setOnClickListener{
            val intent = Intent(this, ExerciseListActivity::class.java)
            startActivity(intent)
        }

        binding.statsLink.setOnClickListener{
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        binding.workoutsLink.setOnClickListener{
            val intent = Intent(this, WorkoutListActivity::class.java)
            startActivity(intent)
        }

    }
    private fun showUser(){
        val uid = firebaseAuth.uid.toString()
        database= FirebaseDatabase.getInstance().getReference("Users")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val username=it.child("username").value
                val expLevel=it.child("exp").value
                binding.usernameText.text = username.toString()
                binding.userLevelValue.text=expLevel.toString()
            }
        }
    }

    private fun showTopWorkouts(workouts: List<Workout>) {
        showWorkouts(workouts, binding.workoutsLayout)
    }

    private fun showPopularWorkouts(workouts: List<Workout>) {
        showWorkouts(workouts, binding.workoutsPopularLayout)
    }

    private fun showWorkouts(workouts: List<Workout>, layout: LinearLayout) {
        val workoutsLayout = layout
        for (workout in workouts) {

            val workoutCard = createWorkoutCard()
            workoutCard.elevation = 0.toPx().toFloat()

            val workoutLayout = createWorkoutCardLinearLayout()

            // Name
            val workoutName = createWorkoutNameText(workout)
            workoutLayout.addView(workoutName)

            // Stats
            val statsLayout = createWorkoutStatsLinearLayout()

            // Exercises Stat
            val exercisesStatLayout = createWorkoutStatLinearLayout()
            val exercisesStatValue = createStatValueText("--")  // TODO
            val exercisesStatLabel = createStatLabelText(getString(R.string.number_exercises_label))
            exercisesStatLayout.addView(exercisesStatValue)
            exercisesStatLayout.addView(exercisesStatLabel)
            statsLayout.addView(exercisesStatLayout)

            // Kcal Stat
            val kcalStatLayout = createWorkoutStatLinearLayout()
            val kcalStatValue = createStatValueText("--")  // TODO
            val kcalStatLabel = createStatLabelText(getString(R.string.calories_data_label))
            kcalStatLayout.addView(kcalStatValue)
            kcalStatLayout.addView(kcalStatLabel)
            statsLayout.addView(kcalStatLayout)

            // BPM Stat
            val bpmStatLayout = createWorkoutStatLinearLayout()
            val bpmStatValue = createStatValueText("--")  // TODO
            val bpmStatLabel = createStatLabelText(getString(R.string.bpm_data_label))
            bpmStatLayout.addView(bpmStatValue)
            bpmStatLayout.addView(bpmStatLabel)
            statsLayout.addView(bpmStatLayout)

            workoutLayout.addView(statsLayout)
            workoutCard.addView(workoutLayout)
            workoutsLayout.addView(workoutCard)
            workoutCard.setOnClickListener{
                val intent = Intent(this, WorkoutPlayActivity::class.java)
                intent.putExtra("workout",workout /*as java.io.Serializable*/)
                startActivity(intent)

            }
        }
    }

    private fun createWorkoutCard(): CardView {

        val workoutCard = CardView(applicationContext)

        val params = LinearLayout.LayoutParams(250.toPx(), LayoutParams.MATCH_PARENT)

        workoutCard.radius = 10.toPx().toFloat()
        params.marginEnd = 20.toPx()
        workoutCard.layoutParams = params
        workoutCard.setCardBackgroundColor(Color.TRANSPARENT)

        return workoutCard

    }

    private fun createWorkoutCardLinearLayout(): LinearLayout {
        val workoutLayout = LinearLayout(applicationContext)
        workoutLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        workoutLayout.orientation = VERTICAL
        workoutLayout.setPadding(20.toPx(), 30.toPx(), 20.toPx(), 20.toPx())
        val test = listOf(R.drawable.arms_background, R.drawable.core_background, R.drawable.legs_background,
        R.drawable.mind_background)
        workoutLayout.background = theme.getDrawable(test.random())
        return workoutLayout
    }

    private fun createWorkoutNameText(workout: Workout): TextView {

        val workoutNameView = TextView(applicationContext)
        workoutNameView.text = workout.name
        workoutNameView.setTextColor(Color.WHITE)
        workoutNameView.textAlignment = TEXT_ALIGNMENT_TEXT_START
        workoutNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
        workoutNameView.typeface = Typeface.create("Lato Bold", Typeface.BOLD)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 20.toPx()
        params.marginStart = 10.toPx()
        workoutNameView.layoutParams = params

        return workoutNameView
    }

    private fun createWorkoutStatsLinearLayout(): LinearLayout {
        val workoutStatsLayout = LinearLayout(applicationContext)
        workoutStatsLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        workoutStatsLayout.orientation = HORIZONTAL
        return workoutStatsLayout
    }

    private fun createWorkoutStatLinearLayout(): LinearLayout {
        val workoutStatLayout = LinearLayout(applicationContext)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.weight = 1f
        workoutStatLayout.layoutParams = layoutParams
        workoutStatLayout.orientation = VERTICAL
        return workoutStatLayout
    }

    private fun createStatValueText(value: String): TextView {

        val workoutNameView = TextView(applicationContext)
        workoutNameView.text = value
        workoutNameView.setTextColor(Color.WHITE)
        workoutNameView.textAlignment = TEXT_ALIGNMENT_CENTER
        workoutNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
        workoutNameView.typeface = Typeface.create("Lato", Typeface.BOLD)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        workoutNameView.layoutParams = params

        return workoutNameView
    }

    override fun onBackPressed() {

    }

    private fun createStatLabelText(value: String): TextView {

        val workoutNameView = TextView(applicationContext)
        workoutNameView.text = value
        workoutNameView.setTextColor(Color.WHITE)
        workoutNameView.textAlignment = TEXT_ALIGNMENT_CENTER
        workoutNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        workoutNameView.typeface = Typeface.create("Lato", Typeface.BOLD)
        workoutNameView.isAllCaps = true

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        workoutNameView.layoutParams = params

        return workoutNameView
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

}