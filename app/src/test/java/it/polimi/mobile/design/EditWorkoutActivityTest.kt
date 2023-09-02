package it.polimi.mobile.design

import org.junit.Assert.*

import android.content.Intent
import android.text.Editable
import it.polimi.mobile.design.databinding.ActivityEditWorkoutBinding
import it.polimi.mobile.design.entities.Exercise
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast

class EditWorkoutActivityTest {

    private lateinit var activity: EditWorkoutActivity

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(EditWorkoutActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
    }
}
