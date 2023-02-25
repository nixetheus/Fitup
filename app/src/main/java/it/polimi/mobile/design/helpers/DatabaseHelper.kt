package it.polimi.mobile.design.helpers

import com.google.firebase.auth.FirebaseAuth
import it.polimi.mobile.design.entities.Workout
import com.google.firebase.database.DataSnapshot
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.User
import it.polimi.mobile.design.entities.WorkoutExercise


class DatabaseHelper() {

    private var firebaseAuth = FirebaseAuth.getInstance()

    private var instance: DatabaseHelper? = null
    fun getInstance(): DatabaseHelper? {
        if (instance == null) {
            synchronized(DatabaseHelper::class.java) {
                if (instance == null) {
                    instance = DatabaseHelper()
                }
            }
        }
        return instance
    }

    fun getWorkoutsFromSnapshot(snapshot: DataSnapshot) : ArrayList<Workout> {
        val workouts = ArrayList<Workout>()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val workout = child.getValue(Workout::class.java)
                if (workout != null && workout.userId == firebaseAuth.uid)
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
                if (exercise != null) exercises.add(exercise)
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

}