package it.polimi.mobile.design


import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.helpers.HelperFunctions
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelperFunctionsTest {

    private lateinit var helperFunctions: HelperFunctions

    @Before
    fun setup() {
        helperFunctions = HelperFunctions()
    }

    @Test
    fun testSecondsToFormatString() {
        assertEquals("01:00", helperFunctions.secondsToFormatString(60))
        assertEquals("00:00", helperFunctions.secondsToFormatString(0))
        assertEquals("02:35", helperFunctions.secondsToFormatString(155))
    }

    @Test
    fun testParseIntInput() {
        assertEquals(10, helperFunctions.parseIntInput("10"))
        assertEquals(0, helperFunctions.parseIntInput(""))
        assertEquals(0, helperFunctions.parseIntInput("abc"))
    }

    @Test
    fun testParseFloatInput() {
        assertEquals(5.5f, helperFunctions.parseFloatInput("5.5"))
        assertEquals(0f, helperFunctions.parseFloatInput(""))
        assertEquals(0f, helperFunctions.parseFloatInput("abc"))
    }

    @Test
    fun testGetWorkoutType() {
        val exercisesTypes1 = mutableListOf(1, 2, 3, 4)
        assertEquals(
            ExerciseType.CHEST.ordinal,
            helperFunctions.getWorkoutType(exercisesTypes1)
        )

        val exercisesTypes2 = mutableListOf(1, 2, 3, 3)
        assertEquals(ExerciseType.FULL_BODY.ordinal, helperFunctions.getWorkoutType(exercisesTypes2))

        val exercisesTypes3 = mutableListOf(1, 1, 1, 1)
        assertEquals(
            ExerciseType.FULL_BODY.ordinal,
            helperFunctions.getWorkoutType(exercisesTypes3)
        )
    }

    @Test
    fun testGetSerializableExtra() {
        val intent = Intent().apply {
            putExtra("testExtra", 5)
        }
        val value: Int? = helperFunctions.getSerializableExtra(intent, "testExtra")
        assertEquals(5, value)
    }
}
