package it.polimi.mobile.design


import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.database.DataSnapshot
import it.polimi.mobile.design.entities.*
import it.polimi.mobile.design.helpers.DatabaseHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {

    private val helperDB = DatabaseHelper().getInstance()
    private lateinit var activityScenario: ActivityScenario<CentralActivity>

    // Initialize Activity for Firebase
    @Before
    fun setup() {
        Intents.init()
        activityScenario = ActivityScenario.launch(CentralActivity::class.java)
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testGetWorkoutsFromSnapshotEmpty() {

        val snapshots  = mutableListOf<DataSnapshot>()
        val workouts   = listOf<Workout>()

        val workoutSnap: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(workoutSnap.exists()).thenReturn(true)
        `when`(workoutSnap.getValue(Workout::class.java)).thenReturn(null)
        snapshots.add(workoutSnap)

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(workouts, helperDB.getWorkoutsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetWorkoutsFromSnapshotFull() {

        var workoutSnap: DataSnapshot
        val uid        = DatabaseHelper().getInstance().getFirebaseAuth().uid
        val snapshots  = mutableListOf<DataSnapshot>()
        val workouts   = listOf(Workout(userId = uid), Workout(userId = uid), Workout(userId = uid))

        for (workout in workouts) {
            workoutSnap = mock(DataSnapshot::class.java)
            `when`(workoutSnap.exists()).thenReturn(true)
            `when`(workoutSnap.getValue(Workout::class.java)).thenReturn(workout)
            snapshots.add(workoutSnap)
        }

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(workouts, helperDB.getWorkoutsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetExercisesFromSnapshotEmpty() {

        val snapshots  = mutableListOf<DataSnapshot>()
        val exercises   = listOf<Exercise>()

        val exerciseSnap: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(exerciseSnap.exists()).thenReturn(true)
        `when`(exerciseSnap.getValue(Exercise::class.java)).thenReturn(null)
        snapshots.add(exerciseSnap)

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(exercises, helperDB.getWorkoutsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetExercisesFromSnapshotFull() {

        var exerciseSnap: DataSnapshot
        val snapshots  = mutableListOf<DataSnapshot>()
        val exercises   = listOf(Exercise(), Exercise(), Exercise())

        for (exercise in exercises) {
            exerciseSnap = mock(DataSnapshot::class.java)
            `when`(exerciseSnap.exists()).thenReturn(true)
            `when`(exerciseSnap.getValue(Exercise::class.java)).thenReturn(exercise)
            snapshots.add(exerciseSnap)
        }

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(exercises, helperDB.getExercisesFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetWorkoutExercisesFromSnapshotEmpty() {

        val snapshots  = mutableListOf<DataSnapshot>()
        val workoutExercises = listOf<WorkoutExercise>()

        val workoutExerciseSnap: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(workoutExerciseSnap.exists()).thenReturn(true)
        `when`(workoutExerciseSnap.getValue(WorkoutExercise::class.java)).thenReturn(null)
        snapshots.add(workoutExerciseSnap)

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(workoutExercises, helperDB.getAllWorkoutsExercisesFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetWorkoutExercisesFromSnapshotFull() {

        var workoutExerciseSnap: DataSnapshot
        val snapshots  = mutableListOf<DataSnapshot>()
        val workoutExercises = listOf(WorkoutExercise(), WorkoutExercise(), WorkoutExercise())

        for (workoutExercise in workoutExercises) {
            workoutExerciseSnap = mock(DataSnapshot::class.java)
            `when`(workoutExerciseSnap.exists()).thenReturn(true)
            `when`(workoutExerciseSnap.getValue(WorkoutExercise::class.java)).thenReturn(workoutExercise)
            snapshots.add(workoutExerciseSnap)
        }

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(workoutExercises, helperDB.getAllWorkoutsExercisesFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetUsersFromSnapshot() {

        val user = User()

        val userSnap: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(userSnap.exists()).thenReturn(true)
        `when`(userSnap.getValue(User::class.java)).thenReturn(user)

        assertEquals(user, helperDB.getUserFromSnapshot(userSnap))
    }

    @Test
    fun testGetDataPointsFromSnapshotEmpty() {

        val snapshots  = mutableListOf<DataSnapshot>()
        val dataPoints = listOf<DataPoint>()

        val dataPointSnap: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataPointSnap.exists()).thenReturn(true)
        `when`(dataPointSnap.getValue(DataPoint::class.java)).thenReturn(null)
        snapshots.add(dataPointSnap)

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(dataPoints, helperDB.getPointsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetDataPointsFromSnapshotFull() {

        var dataPointSnap: DataSnapshot
        val snapshots  = mutableListOf<DataSnapshot>()
        val dataPoints = listOf(DataPoint(), DataPoint(), DataPoint())

        for (dataPoint in dataPoints) {
            dataPointSnap = mock(DataSnapshot::class.java)
            `when`(dataPointSnap.exists()).thenReturn(true)
            `when`(dataPointSnap.getValue(DataPoint::class.java)).thenReturn(dataPoint)
            snapshots.add(dataPointSnap)
        }

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(dataPoints, helperDB.getPointsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetGraphsFromSnapshotEmpty() {

        val snapshots  = mutableListOf<DataSnapshot>()
        val graphs = listOf<Graph>()

        val graphSnap: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(graphSnap.exists()).thenReturn(true)
        `when`(graphSnap.getValue(Graph::class.java)).thenReturn(null)
        snapshots.add(graphSnap)

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(graphs, helperDB.getGraphsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetGraphsFromSnapshotFull() {

        var graphSnap: DataSnapshot
        val snapshots  = mutableListOf<DataSnapshot>()
        val graphs = listOf(Graph(), Graph(), Graph())

        for (graph in graphs) {
            graphSnap = mock(DataSnapshot::class.java)
            `when`(graphSnap.exists()).thenReturn(true)
            `when`(graphSnap.getValue(Graph::class.java)).thenReturn(graph)
            snapshots.add(graphSnap)
        }

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(graphs, helperDB.getGraphsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetAchievementsFromSnapshotEmpty() {

        val snapshots  = mutableListOf<DataSnapshot>()
        val achievements = listOf<Achievement>()

        val achievementSnap: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(achievementSnap.exists()).thenReturn(true)
        `when`(achievementSnap.getValue(Achievement::class.java)).thenReturn(null)
        snapshots.add(achievementSnap)

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(achievements, helperDB.getAchievementsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetAchievementsFromSnapshotFull() {

        var achievementSnap: DataSnapshot
        val snapshots  = mutableListOf<DataSnapshot>()
        val achievements = listOf(Achievement(), Achievement(), Achievement())

        for (achievement in achievements) {
            achievementSnap = mock(DataSnapshot::class.java)
            `when`(achievementSnap.exists()).thenReturn(true)
            `when`(achievementSnap.getValue(Achievement::class.java)).thenReturn(achievement)
            snapshots.add(achievementSnap)
        }

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(achievements, helperDB.getAchievementsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetUserAchievementsFromSnapshotEmpty() {

        val snapshots  = mutableListOf<DataSnapshot>()
        val userAchievements = listOf<UserAchievements>()

        val userAchievementSnap: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(userAchievementSnap.exists()).thenReturn(true)
        `when`(userAchievementSnap.getValue(UserAchievements::class.java)).thenReturn(null)
        snapshots.add(userAchievementSnap)

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(userAchievements, helperDB.getUserAchievementsFromSnapshot(dataSnapshot))
    }

    @Test
    fun testGetUserAchievementsFromSnapshotFull() {

        var userAchievementSnap: DataSnapshot
        val snapshots  = mutableListOf<DataSnapshot>()
        val userAchievements = listOf(UserAchievements(), UserAchievements(), UserAchievements())

        for (userAchievement in userAchievements) {
            userAchievementSnap = mock(DataSnapshot::class.java)
            `when`(userAchievementSnap.exists()).thenReturn(true)
            `when`(userAchievementSnap.getValue(UserAchievements::class.java)).thenReturn(userAchievement)
            snapshots.add(userAchievementSnap)
        }

        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(dataSnapshot.exists()).thenReturn(true)
        `when`(dataSnapshot.children).thenReturn(snapshots)

        assertEquals(userAchievements, helperDB.getUserAchievementsFromSnapshot(dataSnapshot))
    }


}

