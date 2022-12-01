package it.polimi.mobile.design


import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View.TEXT_ALIGNMENT_TEXT_END
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toolbar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.entities.Workout
import java.sql.Time


class CentralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCentralBinding
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCentralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        val uid = firebaseAuth.uid.toString()
        database= FirebaseDatabase.getInstance().getReference("Users")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val username=it.child("username").value
                binding.textView4.text="Welcome "+username.toString()
            }
        }

        binding.exercisesLink.setOnClickListener{
            val intent = Intent(this, ExerciseListActivity::class.java)
            startActivity(intent)
        }

        binding.statsLinkText.setOnClickListener{

        }

        binding.workoutLinkText.setOnClickListener{
            val intent = Intent(this, WorkoutListActivity::class.java)
            startActivity(intent)
        }

        showTopWorkouts(listOf(Workout("143", Time(5675643421)), Workout("123", Time(5675643421))))
    }

    private fun showTopWorkouts(workouts: List<Workout>) {

        val workoutsLayout = findViewById<LinearLayout>(R.id.workoutsLayout)
        for (workout in workouts) {

            val workoutCard = createWorkoutCard()
            workoutCard.background = ColorDrawable(Color.RED)

            val workoutLayout = createWorkoutCardLinearLayout()

            // Name
            val workoutName = createWorkoutNameText()
            workoutLayout.addView(workoutName)

            // Stats
            val statsLayout = createWorkoutStatsLinearLayout()

            // Exercises Stat
            val exercisesStatLayout = createWorkoutStatLinearLayout()
            // TODO
            statsLayout.addView(exercisesStatLayout)

            workoutLayout.addView(statsLayout)
            workoutCard.addView(workoutLayout)
            workoutsLayout.addView(workoutCard)
        }
    }

    private fun createWorkoutCard(): CardView {

        val workoutCard = CardView(applicationContext)

        val params = LinearLayout.LayoutParams(250.toPx(), LayoutParams.MATCH_PARENT)

        workoutCard.radius = 15.toPx().toFloat()
        params.marginEnd = 20.toPx()
        workoutCard.layoutParams = params
        workoutCard.setContentPadding(20.toPx(), 20.toPx(), 20.toPx(), 20.toPx())

        return workoutCard

    }

    private fun createWorkoutCardLinearLayout(): LinearLayout {
        val workoutLayout = LinearLayout(applicationContext)
        workoutLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        workoutLayout.orientation = VERTICAL
        return workoutLayout
    }

    private fun createWorkoutNameText(): TextView {

        val workoutNameView = TextView(applicationContext)
        workoutNameView.text = "Workout Name"
        workoutNameView.setTextColor(Color.BLACK)
        workoutNameView.textAlignment = TEXT_ALIGNMENT_TEXT_START
        workoutNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        workoutNameView.typeface = Typeface.create("Lato Bold", Typeface.BOLD)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        params.bottomMargin = 25.toPx()
        workoutNameView.layoutParams = params

        return workoutNameView
    }

    private fun createWorkoutStatsLinearLayout(): LinearLayout {
        val workoutStatsLayout = LinearLayout(applicationContext)
        workoutStatsLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        workoutStatsLayout.orientation = VERTICAL
        return workoutStatsLayout
    }

    private fun createWorkoutStatLinearLayout(): LinearLayout {
        val workoutStatLayout = LinearLayout(applicationContext)
        workoutStatLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        workoutStatLayout.orientation = VERTICAL
        return workoutStatLayout
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

}