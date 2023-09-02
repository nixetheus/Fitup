package it.polimi.mobile.design

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import it.polimi.mobile.design.SettingsActivity
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test

class SettingsActivityTest {
    var auth =  FirebaseAuth.getInstance()
    private lateinit var appContext: Context

    @Before
    fun setUp() {

        ActivityScenario.launch(SettingsActivity::class.java)
    }


    @Test
    fun testLogout() {

        assertNotNull(auth)
        Espresso.onView(ViewMatchers.withText("Logout")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Yes")).perform(ViewActions.click())
        assertNull(auth.currentUser)


    }


}
