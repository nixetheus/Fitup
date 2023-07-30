package it.polimi.mobile.design

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseListActivityTest {

    private lateinit var activityScenario: ActivityScenario<ExerciseListActivity>

    @Before
    fun setup() {
        Intents.init()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, ExerciseListActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testOpenAddExerciseLayout() {
        // Perform click on the plus exercise button
        onView(withId(R.id.addExerciseButton)).perform(click())
        // Verify that add exercise layout is visible
        Thread.sleep(1000)
        onView(withId(R.id.addExerciseCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun testCloseAddExerciseLayout() {
        // Perform click on the plus exercise button
        onView(withId(R.id.addExerciseButton)).perform(click())
        // Verify that add exercise layout is visible
        Thread.sleep(1000)
        onView(withId(R.id.addExerciseCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        // Perform click on the close button
        onView(withId(R.id.addExerciseClose)).perform(click())
        Thread.sleep(1000)
        // Verify that add exercise layout is visible
        onView(withId(R.id.addExerciseCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    // Navigation Tests
    @Test
    fun testClickOnHome() {
        // Perform click on the home button
        onView(withId(R.id.homeButton)).perform(click())
        // Verify that Central Activity is started
        Thread.sleep(1000)
        Intents.intended(IntentMatchers.hasComponent(CentralActivity::class.java.name))
    }
}
