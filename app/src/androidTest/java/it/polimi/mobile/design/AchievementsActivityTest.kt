package it.polimi.mobile.design

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AchievementsActivityTest {

    private lateinit var activityScenario: ActivityScenario<AchievementsActivity>

    @Before
    fun setup() {
        Intents.init()
        activityScenario = ActivityScenario.launch(AchievementsActivity::class.java)
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testClickOnHomeButtonShouldStartCentralActivity() {

        // Perform click on the home button
        onView(withId(R.id.homeButton)).perform(click())

        // Verify that CentralActivity is started
        Intents.intended(hasComponent(CentralActivity::class.java.name))
    }

    // Add more UI test cases with intent verification as needed
}
