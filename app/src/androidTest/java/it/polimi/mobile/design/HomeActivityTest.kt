package it.polimi.mobile.design

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    private lateinit var activityScenario: ActivityScenario<HomeActivity>

    @Before
    fun setup() {
        Intents.init()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, HomeActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    // Navigation Tests
    @Test
    fun testNextButtonNoInfo() {
        // Perform click on the next button with empty fields
        onView(withId(R.id.nextBtn)).perform(click())
        // Verify that activity does not change
        assert(activityScenario.state == Lifecycle.State.RESUMED)
    }
}
