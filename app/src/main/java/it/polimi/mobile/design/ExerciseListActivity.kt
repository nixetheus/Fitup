package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityExerciseListBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.enum.ExerciseType

class ExerciseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseListBinding
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var exerciseArrayList: ArrayList<Exercise>

    private lateinit var tempExerciseArrayList:ArrayList<Exercise>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        exerciseArrayList= arrayListOf<Exercise>()


        tempExerciseArrayList= arrayListOf<Exercise>()
        database=FirebaseDatabase.getInstance().getReference("Exercise")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseArrayList.clear()
                if (snapshot.exists()){
                    for (exerciseSnap in snapshot.children){
                        val exerciseData=exerciseSnap.getValue(Exercise::class.java)
                        if (exerciseData!=null)
                            exerciseArrayList.add(exerciseData!!)
                    }
                    showExercises(exerciseArrayList)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        binding.confirmAddExerciseBtn.setOnClickListener{
            createExercise()
            binding.addExerciseCard.visibility=View.GONE
        }
        binding.addExerciseButton.setOnClickListener{
            binding.addExerciseCard.visibility=View.VISIBLE
        }
        binding.addExerciseClose.setOnClickListener{
            binding.addExerciseCard.visibility=View.GONE
        }

        binding.searchExercise.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                /*binding.searchExercise.clearFocus()
                if(exerciseStringList.contains(p0)){

                    for (exercise in exerciseArrayList) {
                        if (exercise.name == p0 || exercise.type.toString() == p0)
                            tempExerciseArrayList.add(exercise)
                    }
                    binding.exercisesListLayout.removeAllViews()
                    showExercises(tempExerciseArrayList)

                }*/
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                tempExerciseArrayList.clear()
                for (exercise in exerciseArrayList) {
                        if (exercise.name!!.contains(p0.toString()) || exercise.type.toString().contains(p0.toString()))
                            tempExerciseArrayList.add(exercise)
                }

                showExercises(tempExerciseArrayList)


                return false

            }

        })
    }
    private fun createExercise(){
        val exType: ExerciseType
        val name=binding.exerciseNameField.text.toString()
        val kcalPerReps=binding.kcalInputValue.text.toString()
        val exerciseType= binding.typeOfEx.selectedItem.toString()
        exType= if (exerciseType=="Arms")
            ExerciseType.ARMS
        else if (exerciseType=="Legs")
            ExerciseType.LEGS
        else if(exerciseType=="Chest")
            ExerciseType.CHEST
        else
            ExerciseType.ABDOMEN
        val exp=binding.expInputValue.text.toString()

        database = FirebaseDatabase.getInstance().getReference("Exercise")
        val eId = database.push().key!!
        if (name.isNotEmpty()&&kcalPerReps.isNotEmpty()&&exp.isNotEmpty()){
        val exercise = Exercise(eId, name, 0f, exType, 0)
        database.child(name).setValue(exercise).addOnSuccessListener {
            Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ExerciseListActivity::class.java)
            startActivity(intent)
            finish()
        }
        }
        else Toast.makeText(this, "Fill in all fields to continue!!", Toast.LENGTH_SHORT).show()
    }

    private fun showExercises(exercises: List<Exercise>) {
        binding.exercisesListLayout.removeAllViews()
        val exercisesLayout = binding.exercisesListLayout
        for (exercise in exercises) {

            val exerciseCard = createExerciseCard()
            exerciseCard.setCardBackgroundColor(Color.RED)

            val exerciseLayout = createExerciseCardConstraintLayout()

            // Image
            // TODO
            
            // Menu
            val exerciseMenu = createExerciseMenuLinearLayout()

            // Name
            val exerciseName = createExerciseNameText(exercise)
            exerciseMenu.addView(exerciseName)
            
            // Setup
            val set = ConstraintSet()
            exerciseMenu.id = View.generateViewId()
            exerciseLayout.addView(exerciseMenu, 0)
            
            set.clone(exerciseLayout)
            set.connect(exerciseMenu.id, ConstraintSet.TOP, exerciseLayout.id, ConstraintSet.TOP, 0)
            set.connect(exerciseMenu.id, ConstraintSet.BOTTOM, exerciseLayout.id, ConstraintSet.BOTTOM, 0)
            set.connect(exerciseMenu.id, ConstraintSet.LEFT, exerciseLayout.id, ConstraintSet.LEFT, 0)
            set.connect(exerciseMenu.id, ConstraintSet.RIGHT, exerciseLayout.id, ConstraintSet.RIGHT, 0)
            set.applyTo(exerciseLayout)

            exerciseCard.addView(exerciseLayout)
            exercisesLayout.addView(exerciseCard)
        }
    }

    private fun createExerciseCard(): CardView {

        val exerciseCard = CardView(applicationContext)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT)

        exerciseCard.radius = 15.toPx().toFloat()
        params.bottomMargin = 20.toPx()
        exerciseCard.layoutParams = params

        return exerciseCard

    }

    private fun createExerciseCardConstraintLayout(): ConstraintLayout {
        val exerciseLayout = ConstraintLayout(applicationContext)
        exerciseLayout.layoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        return exerciseLayout
    }

    private fun createExerciseImage(): ImageView {
        return ImageView(applicationContext)
        // TODO
    }

    private fun createExerciseMenuLinearLayout(): LinearLayout {
        val exerciseMenuLayout = LinearLayout(applicationContext)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        exerciseMenuLayout.background = ColorDrawable(Color.BLUE)
        exerciseMenuLayout.setPadding(20.toPx())
        exerciseMenuLayout.layoutParams = layoutParams
        exerciseMenuLayout.orientation = LinearLayout.VERTICAL
        return exerciseMenuLayout
    }

    private fun createExerciseNameText(exercise: Exercise): TextView {

        val exerciseNameView = TextView(applicationContext)
        exerciseNameView.text = exercise.name
        exerciseNameView.setTextColor(Color.WHITE)
        exerciseNameView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        exerciseNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        exerciseNameView.typeface = Typeface.create("Lato Bold", Typeface.BOLD)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        exerciseNameView.layoutParams = params

        return exerciseNameView
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}