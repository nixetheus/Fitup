package it.polimi.mobile.design


import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CentralActivityTest {

    private lateinit var activityScenario: ActivityScenario<CentralActivity>

    @Before
    fun setUp() {
        Intents.init()
        activityScenario = ActivityScenario.launch(CentralActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        activityScenario.close()
    }

    // Filter Click Test
    @Test
    fun testClickOnFilter() {

        // Get initial filters size
        var initialSizeFilter = 0
        activityScenario.onActivity { activity ->
            initialSizeFilter = activity.getFiltersSize()
        }

        // Perform click on the filter card
        Espresso.onView(allOf(withId(R.id.filterCard),
            withChild(withText("Arms")))).perform(ViewActions.click())

        // Verify that filters attribute as one more as size
        activityScenario.onActivity { activity ->
            Assert.assertEquals(initialSizeFilter + 1, activity.getFiltersSize())
        }
    }

    // Navigation Tests
    @Test
    fun testClickOnSettings() {
        // Perform click on the settings button
        Espresso.onView(withId(R.id.userImage)).perform(ViewActions.click())
        // Verify that Settings Activity is started
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun testClickOnAchievements() {
        // Perform click on the achievements button
        Espresso.onView(withId(R.id.trophyLink)).perform(ViewActions.click())
        // Verify that Achievements Activity is started
        Intents.intended(IntentMatchers.hasComponent(AchievementsActivity::class.java.name))
    }

    @Test
    fun testClickOnWorkouts() {
        // Perform click on the workouts button
        Espresso.onView(withId(R.id.workoutsLink)).perform(ViewActions.click())
        // Verify that Workout List Activity is started
        Intents.intended(IntentMatchers.hasComponent(WorkoutListActivity::class.java.name))
    }

    @Test
    fun testClickOnStats() {
        // Perform click on the stats button
        Espresso.onView(withId(R.id.statsLink)).perform(ViewActions.click())
        // Verify that Stats Activity is started
        Intents.intended(IntentMatchers.hasComponent(StatsActivity::class.java.name))
    }

    @Test
    fun testClickOnExercises() {
        // Perform click on the exercises button
        Espresso.onView(withId(R.id.exercisesLink)).perform(ViewActions.click())
        // Verify that Exercise List Activity is started
        Intents.intended(IntentMatchers.hasComponent(ExerciseListActivity::class.java.name))
    }
}