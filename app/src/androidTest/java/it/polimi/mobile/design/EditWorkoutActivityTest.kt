package it.polimi.mobile.design

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.helpers.DatabaseHelper
import kotlinx.coroutines.delay
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Math.abs
import kotlin.random.Random
import kotlin.random.nextInt

@RunWith(AndroidJUnit4::class)
class EditWorkoutActivityTest {

    private lateinit var activityScenario: ActivityScenario<EditWorkoutActivity>

    @Before
    fun setup() {

        Intents.init()

        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, EditWorkoutActivity::class.java)
        intent.putExtra("Workout", createFakeWorkout())

        activityScenario = ActivityScenario.launch(intent)
    }

    private fun createFakeWorkout() : Workout {
        return Workout(
            "-Na7Mh7plP_oDVrlVw-c", DatabaseHelper().getFirebaseAuth().uid, "Strength - Total Body 1",
            "hip hop", -4, 0, 0f, 0f, 0, mutableListOf(1, 1, 0, 0))
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testCloseAddExerciseLayout() {
        Thread.sleep(1500)
        // Perform click on the plus exercise button
        onView(withId(R.id.openAddExerciseLayout)).perform(click())
        // Verify that add exercise layout is visible
        Thread.sleep(1500)
        onView(withId(R.id.addExerciseToWorkoutCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        // Perform click on close button
        onView(withId(R.id.addExerciseWorkoutClose)).perform(click())
        // Verify that add exercise layout is gone
        Thread.sleep(1500)
        onView(withId(R.id.addExerciseToWorkoutCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    // Navigation Tests
    @Test
    fun testClickOnHome() {
        // Perform click on the home button
        onView(withId(R.id.homeButton)).perform(click())
        Thread.sleep(2000)
        // Verify that Central Activity is started
        Intents.intended(IntentMatchers.hasComponent(CentralActivity::class.java.name))
    }
}
