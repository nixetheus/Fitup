package it.polimi.mobile.design

import android.content.Intent
import android.widget.Chronometer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import it.polimi.mobile.design.entities.Workout

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkoutPlayActivityTest {
    private lateinit var activityScenario: ActivityScenario<WorkoutPlayActivity>
    private lateinit var fakeWorkout: Workout

    @Before
    fun setup() {


        fakeWorkout = createFakeWorkout()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            WorkoutPlayActivity::class.java
        )
        intent.putExtra("Workout", fakeWorkout)
        intent.putExtra("exp", 0F)
        Intents.init()

        activityScenario = ActivityScenario.launch(intent)
    }


    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }


    @Test
    fun testWorkoutName(){

        // verify initial chrono setup
        onView(withId(R.id.playWorkoutName)).check(matches(withText(fakeWorkout.name)))



    }

    @Test
    fun testExerciseName(){


        // verify the name
        onView(withId(R.id.currentExerciseName)).check(matches(withText("Plank")))
        //onView(withId(R.id.playPauseButton)).perform(ViewActions.click())

        //perform next exercise click
        onView(withId(R.id.nextButton)).perform(ViewActions.click())
        //workout finished
        onView(withId(R.id.stopButton)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(WorkoutEndActivity::class.java.name))

    }

    @Test
    fun testStartChronometerExercise() {


        // verify initial chrono setup
        onView(withId(R.id.exerciseCounter)).check(matches(withText("00:00")))
        //onView(withId(R.id.playPauseButton)).perform(ViewActions.click())

        // wait 1 sec
        Thread.sleep(1000)

        // Verify
        onView(withId(R.id.exerciseCounter)).check(matches((withText("00:01"))))
        // wait 1 sec
        Thread.sleep(1000)

        // Verify
        onView(withId(R.id.exerciseCounter)).check(matches((withText("00:02"))))
        //perform next exercise click
        onView(withId(R.id.nextButton)).perform(ViewActions.click())
        // verify that chrono being initialize to 00:00
        //onView(withId(R.id.exerciseCounter)).check(matches((withText("00:00"))))


    }
    @Test
    fun testStartChronometer() {




        // Verify initial chrono setup
        onView(withId(R.id.workoutTimeValue)).check(matches(withText("00:00:00")))

       // onView(withId(R.id.playPauseButton)).perform(ViewActions.click())

        // wait 10 seconds
        Thread.sleep(10000)

        // Verify
        onView(withId(R.id.workoutTimeValue)).check(matches((withText("00:00:10"))))

        onView(withId(R.id.nextButton)).perform(ViewActions.click())
        // wait 10 seconds
        Thread.sleep(10000)

        // Verify
        onView(withId(R.id.workoutTimeValue)).check(matches((withText("00:00:20"))))
    }
    private fun createFakeWorkout(): Workout {
        return Workout(
            "-NbpBMpnIZ-P0jzw0T0z",
            "p0YFtxASaFXbjqkxBJVXsAbeZYG3",
            "Abdominal Blast",
            "https://open.spotify.com/playlist/37i9dQZF1DXdURFimg6Blm?si=b9cf156000904184",
            -12,
            1,
            0f,
            0f,
            0,
            mutableListOf(0,1, 0, 0, 0)
        )
    }
}
