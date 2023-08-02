package it.polimi.mobile.design.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.entities.*


class DatabaseHelper {

    // Database Schemas Instances
    private var firebaseAuth    = FirebaseAuth.getInstance()
    val achievementsSchema      = FirebaseDatabase.getInstance().getReference("Achievements")
    val exercisesSchema         = FirebaseDatabase.getInstance().getReference("Exercise")
    val graphsSchema            = FirebaseDatabase.getInstance().getReference("Graphs")
    val pointsSchema            = FirebaseDatabase.getInstance().getReference("Points")
    val userAchievementsSchema  = FirebaseDatabase.getInstance().getReference("UserAchievements")
    val usersSchema             = FirebaseDatabase.getInstance().getReference("Users")
    val workoutsExercisesSchema = FirebaseDatabase.getInstance().getReference("WorkoutExercise")
    val workoutsSchema          = FirebaseDatabase.getInstance().getReference("Workout")

    val acceptedUserIds = listOf(firebaseAuth.uid, "0")

    private var instance: DatabaseHelper? = null
    fun getInstance(): DatabaseHelper {
        if (instance == null) {
            synchronized(DatabaseHelper::class.java) {
                if (instance == null) {
                    instance = DatabaseHelper()
                }
            }
        }
        return instance!!
    }

    fun getWorkoutsFromSnapshot(snapshot: DataSnapshot) : ArrayList<Workout> {
        val workouts = ArrayList<Workout>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val workout = child.getValue(Workout::class.java)
                if (workout != null && workout.userId in acceptedUserIds)
                        workouts.add(workout)
            }
        }
        return workouts
    }

    fun getExercisesFromSnapshot(snapshot: DataSnapshot) : ArrayList<Exercise> {
        val exercises = ArrayList<Exercise>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val exercise = child.getValue(Exercise::class.java)
                if (exercise != null && exercise.uid in acceptedUserIds) exercises.add(exercise)
            }
        }
        return exercises
    }

    fun getWorkoutsExercisesFromSnapshot(snapshot: DataSnapshot, workoutId: String) :
            ArrayList<WorkoutExercise> {

        val workoutExercises = ArrayList<WorkoutExercise>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val workoutExercise = child.getValue(WorkoutExercise::class.java)
                if (workoutExercise != null && workoutExercise.workoutId == workoutId)
                    workoutExercises.add(workoutExercise)
            }
        }
        return workoutExercises
    }

    fun getAllWorkoutsExercisesFromSnapshot(snapshot: DataSnapshot) :
            ArrayList<WorkoutExercise> {

        val workoutExercises = ArrayList<WorkoutExercise>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val workoutExercise = child.getValue(WorkoutExercise::class.java)
                if (workoutExercise != null)
                    workoutExercises.add(workoutExercise)
            }
        }
        return workoutExercises
    }

    fun getUserFromSnapshot(snapshot: DataSnapshot) : User? {
        if (snapshot.exists()) {
            return snapshot.getValue(User::class.java)
        }
        return User()
    }

    fun getPointsFromSnapshot(snapshot: DataSnapshot) : List<DataPoint> {
        val points = ArrayList<DataPoint>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val point = child.getValue(DataPoint::class.java)
                if (point != null && point.userId == firebaseAuth.uid)
                    points.add(point)
            }
        }
        return points
    }

    fun getGraphsFromSnapshot(snapshot: DataSnapshot) : List<Graph> {
        val graphs = ArrayList<Graph>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val graph = child.getValue(Graph::class.java)
                if (graph != null)
                    graphs.add(graph)
            }
        }
        return graphs
    }

    fun getAchievementsFromSnapshot(snapshot: DataSnapshot) : List<Achievement> {
        val achievements = ArrayList<Achievement>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val achievement = child.getValue(Achievement::class.java)
                if (achievement != null)
                    achievements.add(achievement)
            }
        }
        return achievements
    }

    fun getUserAchievementsFromSnapshot(snapshot: DataSnapshot) : List<UserAchievements> {
        val userAchievements = ArrayList<UserAchievements>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val userAchievement = child.getValue(UserAchievements::class.java)
                if (userAchievement != null && userAchievement.userId == firebaseAuth.uid)
                    userAchievements.add(userAchievement)
            }
        }
        return userAchievements
    }

    fun getUserWorkoutDataFromSnapshot(snapshot: DataSnapshot) : List<WorkoutUserData> {
        val workoutUserData = ArrayList<WorkoutUserData>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val workoutData = child.getValue(WorkoutUserData::class.java)
                if (workoutData != null && workoutData.uid in acceptedUserIds)
                    workoutUserData.add(workoutData)
            }
        }
        return workoutUserData
    }

    fun getFirebaseAuth() : FirebaseAuth {
        return firebaseAuth
    }

}