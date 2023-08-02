package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.polimi.mobile.design.databinding.ActivityAchievementsBinding
import it.polimi.mobile.design.databinding.FragmentAchievementBinding
import it.polimi.mobile.design.entities.Achievement
import it.polimi.mobile.design.entities.UserAchievements
import it.polimi.mobile.design.helpers.DatabaseHelper

class AchievementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAchievementsBinding
    private val helperDB = DatabaseHelper().getInstance()

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
        helperDB.achievementsSchema.get().addOnSuccessListener { achievementsSnap ->
            helperDB.userAchievementsSchema.get().addOnSuccessListener { userAchievementsSnap ->

                val achievements = helperDB.getAchievementsFromSnapshot(achievementsSnap)
                val userAchievements = helperDB.getUserAchievementsFromSnapshot(userAchievementsSnap)
                val bestUserAchievements = userAchievements.sortedByDescending { it.tierId }.distinctBy { it.achievementId }

                displayCompletedText(achievements, bestUserAchievements)
                displayAchievements(achievements, bestUserAchievements)
            }
        }
    }

    private fun displayCompletedText(achievements: List<Achievement>,
                                     bestUserAchievements: List<UserAchievements>) {
        val completed = bestUserAchievements.filter{ it.tierId == achievements.find { achievement ->
            achievement.achievementId == it.achievementId }!!.numberOfTiers}
        val tracker = "${completed.size}/${achievements.size}"
        binding.achievementsNumberValue.text = tracker
    }

    private fun displayAchievements(achievements: List<Achievement>,
                                    bestUserAchievements: List<UserAchievements>) {

        for (achievement in achievements) {

            val userTier = bestUserAchievements.find { it.achievementId == achievement.achievementId }?.tierId ?: 0
            val tier = achievement.tiers.getOrElse(userTier - 1) { achievement.tiers.first() }
            val objPerc = "0/${tier.objective}"
            val achievementLayout = FragmentAchievementBinding.inflate(layoutInflater)

            with(achievementLayout) {
                objectivePercValue.text = objPerc
                achievementTierName.text = tier.name!!
                achievementDescription.text =
                    achievement.description!!.replace("%", tier.objective.toString())
                medalCard.setCardBackgroundColor(getAchievementColor(achievement.numberOfTiers!!, userTier))
            }
            binding.achievementsLayout.addView(achievementLayout.root)
        }
    }

    private fun getAchievementColor(tiers: Int, userTier: Int) : Int {
        return when (tiers - userTier) {
            0 -> resources.getColor(R.color.gold, applicationContext.theme)
            1 -> resources.getColor(R.color.silver, applicationContext.theme)
            2 -> resources.getColor(R.color.bronze, applicationContext.theme)
            else -> {resources.getColor(android.R.color.transparent, applicationContext.theme)}
        }
    }
}