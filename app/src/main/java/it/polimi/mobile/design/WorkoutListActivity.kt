package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.adapter.WorkoutAdapter
import it.polimi.mobile.design.databinding.ActivityWorkoutListBinding
import it.polimi.mobile.design.entities.Workout

class WorkoutListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutListBinding
    private lateinit var workoutRecyclerView: RecyclerView
    private lateinit var workoutArrayList: ArrayList<Workout>
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        workoutRecyclerView=binding.recyclerView
        workoutRecyclerView.layoutManager=LinearLayoutManager(this)
        workoutRecyclerView.setHasFixedSize(false)
        workoutArrayList= arrayListOf<Workout>()
        database=FirebaseDatabase.getInstance().getReference("Workout")
        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutArrayList.clear()
                if (snapshot.exists()){
                    for (workSnap in snapshot.children){
                        val workData=workSnap.getValue(Workout::class.java)
                        workoutArrayList.add(workData!!)

                    }
                    workoutAdapter= WorkoutAdapter(workoutArrayList)
                    workoutRecyclerView.adapter=workoutAdapter

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        binding.closeAddWorkout.setOnClickListener{
            binding.workoutAddLayout.visibility = View.GONE


        }
        binding.addWorkoutsButton.setOnClickListener{
            binding.workoutAddLayout.visibility=View.VISIBLE
        }


        EventChangeListener()
        binding.confirmAddWorkoutBtn.setOnClickListener{
            createWorkout()
        }
    }
    private fun EventChangeListener(){
        database=FirebaseDatabase.getInstance().getReference("Workout")

    }
    private fun createWorkout(){
        val name=binding.workoutNameField.text.toString()
        val uid=firebaseAuth.uid.toString()

        database = FirebaseDatabase.getInstance().getReference("Workout")
        val wId = database.push().key!!
        val workout = Workout(wId, uid,name,"random", "hip hop")
        database.child(name).setValue(workout).addOnSuccessListener {
            Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, WorkoutListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showWorkouts(workouts: List<Workout>) {

        val workoutsLayout = binding.workoutsListLayout
        for (workout in workouts) {

            val workoutCard = createWorkoutCard()
            workoutCard.setCardBackgroundColor(Color.RED)

            val workoutLayout = createWorkoutCardLinearLayout()

            // Name
            val workoutName = createWorkoutNameText()
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
        }
    }

    private fun createWorkoutCard(): CardView {

        val workoutCard = CardView(applicationContext)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT)

        workoutCard.radius = 15.toPx().toFloat()
        params.bottomMargin = 30.toPx()
        workoutCard.layoutParams = params
        workoutCard.setContentPadding(20.toPx(), 20.toPx(), 20.toPx(), 20.toPx())

        return workoutCard

    }

    private fun createWorkoutCardLinearLayout(): LinearLayout {
        val workoutLayout = LinearLayout(applicationContext)
        workoutLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        workoutLayout.orientation = LinearLayout.VERTICAL
        return workoutLayout
    }

    private fun createWorkoutNameText(): TextView {

        val workoutNameView = TextView(applicationContext)
        workoutNameView.text = "Workout Name"
        workoutNameView.setTextColor(Color.WHITE)
        workoutNameView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
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
        workoutStatsLayout.orientation = LinearLayout.HORIZONTAL
        return workoutStatsLayout
    }

    private fun createWorkoutStatLinearLayout(): LinearLayout {
        val workoutStatLayout = LinearLayout(applicationContext)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.weight = 1f
        workoutStatLayout.layoutParams = layoutParams
        workoutStatLayout.orientation = LinearLayout.VERTICAL
        return workoutStatLayout
    }

    private fun createStatValueText(value: String): TextView {

        val workoutNameView = TextView(applicationContext)
        workoutNameView.text = value
        workoutNameView.setTextColor(Color.WHITE)
        workoutNameView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        workoutNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
        workoutNameView.typeface = Typeface.create("Lato", Typeface.BOLD)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        workoutNameView.layoutParams = params

        return workoutNameView
    }

    private fun createStatLabelText(value: String): TextView {

        val workoutNameView = TextView(applicationContext)
        workoutNameView.text = value
        workoutNameView.setTextColor(Color.WHITE)
        workoutNameView.textAlignment = View.TEXT_ALIGNMENT_CENTER
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