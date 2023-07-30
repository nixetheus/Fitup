package it.polimi.mobile.design

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.polimi.mobile.design.entities.Workout
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WorkoutListActivityTest {

    private lateinit var activityScenario: ActivityScenario<WorkoutListActivity>

    @Before
    fun setUp() {
        Intents.init()
        activityScenario = ActivityScenario.launch(WorkoutListActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testCreateWorkoutButton() {
        // Perform click on the create workout button
        onView(withId(R.id.addWorkoutsButton)).perform(click())
        // Verify that add workout card is visible
        onView(withId(R.id.addWorkoutCard)).check(matches(isDisplayed()))
    }

    @Test
    fun testShowWorkoutsAndClickOnWorkout() {
        val mockWorkouts = createMockWorkouts()


        ActivityScenario.launch(WorkoutListActivity::class.java).onActivity { activity ->
            activity.showWorkouts(mockWorkouts)
        }

        // verify that workouts are correctly visualized
        onView(withId(R.id.workoutsListLayout)).check(matches(isDisplayed()))
        onView(withText(mockWorkouts[0].name)).check(matches(isDisplayed()))
        onView(withText(mockWorkouts[1].name)).check(matches(isDisplayed()))

        // click on a workout
        onView(withText(mockWorkouts[0].name)).perform(click())

        // Verify that Workout Play Activity is started
        Intents.intended(IntentMatchers.hasComponent(WorkoutPlayActivity::class.java.name))

    }

    private fun createMockWorkouts(): List<Workout> {

        val mockWorkout1 = Workout(
            "workout_id_1",
            "user_id_1",
            "Workout 1",
            "hip hop",
            0,
            null,
            mutableListOf(1, 1, 0, 0)
        )

        val mockWorkout2 = Workout(
            "workout_id_2",
            "user_id_1",
            "Workout 2",
            "legs",
            0,
            null,
            mutableListOf(0, 1, 1, 0)
        )

        return listOf(mockWorkout1, mockWorkout2)
    }

    @Test
    fun testCreateWorkoutWithEmptyName() {
        // Perform click on the create workout button
        onView(withId(R.id.addWorkoutsButton)).perform(click())
        // Perform click on the confirm add workout button with an empty name field
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        // Check if the Toast message is displayed correctly using ShadowToast
       // val shadowToast = Shadows.shadowOf(ShadowToast.getLatestToast())
        //assertEquals("Fill in all fields to continue!!", shadowToast.getText().toString())
    }

    // TODO: Add more tests for other functionalities and interactions

    // Navigation Test
    @Test
    fun testNavigateToCentralActivity() {
        // Perform click on the home button
        onView(withId(R.id.homeButton)).perform(click())
        // Verify that Central Activity is started
        Intents.intended(IntentMatchers.hasComponent(CentralActivity::class.java.name))    }
}
