package it.polimi.mobile.design

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.enum.ExerciseType

class ExerciseListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)
        showExercises(listOf(Exercise("12", "Exercise 1", ExerciseType.ARMS)))
    }

    private fun showExercises(exercises: List<Exercise>) {

        val exercisesLayout = findViewById<LinearLayout>(R.id.exercisesListLayout)
        for (exercise in exercises) {

            val exerciseCard = createExerciseCard()
            exerciseCard.setCardBackgroundColor(Color.RED)

            val exerciseLayout = createExerciseCardConstraintLayout()

            // Image
            // TODO
            
            // Menu
            val exerciseMenu = createExerciseMenuLinearLayout()

            // Name
            val exerciseName = createExerciseNameText()
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

    private fun createExerciseNameText(): TextView {

        val exerciseNameView = TextView(applicationContext)
        exerciseNameView.text = "Exercise Name"
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