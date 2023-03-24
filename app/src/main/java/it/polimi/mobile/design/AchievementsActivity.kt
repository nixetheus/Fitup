package it.polimi.mobile.design

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityAchievementsBinding
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.FragmentAchievementBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutRecentBinding
import it.polimi.mobile.design.entities.Achievement
import it.polimi.mobile.design.entities.UserAchievements
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions
import java.lang.Integer.min

class AchievementsActivity : AppCompatActivity() {

    private var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityAchievementsBinding
    private val databaseInstance = FirebaseDatabase.getInstance()

    private val databaseHelperInstance = DatabaseHelper().getInstance()
    private var achievements = FirebaseDatabase.getInstance().getReference("Achievements")
    private var userAchievements = FirebaseDatabase.getInstance().getReference("UserAchievements")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createBindings()
        retrieveUserAchievements()
    }

    private fun createBindings() {
        binding.homeButton.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveUserAchievements() {
        achievements.get().addOnSuccessListener { achievementsSnap ->
            val achievements = databaseHelperInstance!!.getAchievementsFromSnapshot(achievementsSnap)
            userAchievements.get().addOnSuccessListener { userAchievementsSnap ->
                val userAchievements = databaseHelperInstance.getUserAchievementsFromSnapshot(userAchievementsSnap)
                val bestUserAchievements =
                    userAchievements.sortedByDescending { it.tierId }.distinctBy { it.achievementId }

                val tracker =
                    (bestUserAchievements.filter{
                        it.tierId == achievements.filter {
                                achievement -> achievement.achievementId == it.achievementId }[0].numberOfTiers}.size).toString() +
                            "/" + (achievements.size).toString()

                binding.achievementsNumberValue.text = tracker
                displayAchievements(bestUserAchievements, achievements)
            }
        }
    }

    private fun displayAchievements(bestUserAchievements: List<UserAchievements>,
                                    achievements: List<Achievement>) {
        for (achievement in achievements) {

            var tierName = ""
            var tierObjective = 0
            val desc = achievement.description

            var userTier = 0

            if (!bestUserAchievements.map { it.achievementId }.contains(achievement.achievementId)) {
                tierName = achievement.tiers[0].name!!
                tierObjective = achievement.tiers[0].objective!!
            }
            else {
                userTier = bestUserAchievements.filter { it.achievementId == achievement.achievementId }[0].tierId!!
                tierName = achievement.tiers[userTier - 1].name!!
                tierObjective = achievement.tiers[userTier - 1].objective!!
            }

            // TODO: get user number

            // View
            val objPerc = "0/$tierObjective"
            val achievementLayout = FragmentAchievementBinding.inflate(layoutInflater)

            achievementLayout.achievementTierName.text = tierName
            achievementLayout.achievementDescription.text = desc!!.replace("%", tierObjective.toString())
            achievementLayout.objectivePercValue.text = objPerc

            // Badge color
            when(achievement.numberOfTiers!! - userTier) {
                0 -> achievementLayout.medalCard.setCardBackgroundColor(
                    resources.getColor(R.color.gold, applicationContext.theme))
                1 -> achievementLayout.medalCard.setCardBackgroundColor(
                    resources.getColor(R.color.silver, applicationContext.theme))
                2 -> achievementLayout.medalCard.setCardBackgroundColor(
                    resources.getColor(R.color.bronze, applicationContext.theme))
                else -> achievementLayout.medalCard.setCardBackgroundColor(
                    resources.getColor(R.color.black, applicationContext.theme))
            }

            binding.achievementsLayout.addView(achievementLayout.root)
        }
    }
}