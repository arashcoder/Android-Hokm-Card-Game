package com.hokm.personal.my.hokm.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PrefsHelper {

    private const val KEY_BACKGROUND_ICON = "background_icon"
    private const val KEY_TOTAL_SCORE = "totalScore"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (context !is Application) {
            throw IllegalArgumentException("This should be the application context. Not the activity context")
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun saveBackgroundIcon(iconName: String) {
        prefs.edit()
                .putString(KEY_BACKGROUND_ICON, iconName)
                .apply()
    }

    fun getBackgroundIcon(): String {
        return prefs.getString(KEY_BACKGROUND_ICON, "background")
    }

    fun saveTotalScore(humanWon: Boolean){

        val scores = getScores()
        var scoreHuman = scores.first
        var scoreAI = scores.second
        if(humanWon){
           scoreHuman++
        }
        else{
            scoreAI++
        }
        val score = scoreHuman.toString() + "-" + scoreAI.toString()
        val totalScore = getTotalScore()
        if(shouldUpdateScore(totalScore)){
            totalScore.minus(totalScore.last())
        }

        val newScore = totalScore.plus(score)
        prefs.edit()
                .putStringSet(KEY_TOTAL_SCORE, newScore)
                .apply()

    }

    fun getTotalScore(): Set<String> {
        return prefs.getStringSet(KEY_TOTAL_SCORE, setOf())
    }

    fun getCurrentScore(): Pair<Int, Int>{
        return getScores()//TODO
    }

    private fun shouldUpdateScore(scores: Set<String>): Boolean{
        if(scores.isEmpty()){
            return false
        }
        val lastScore = scores.last()
        val parts = lastScore.split('-')
        val humanScore = parts[0].toInt()
        val aiScore = parts[1].toInt()
        if(humanScore != 7 && aiScore != 7){
            return true
        }
        return false
    }

    private fun getScores():Pair<Int,Int>{
        if(getTotalScore().isEmpty()){
            return Pair(0,0)
        }
        val lastScore = getTotalScore().last()
        val parts = lastScore.split('-')
        val humanScore = parts[0].toInt()
        val aiScore = parts[1].toInt()
        return Pair(humanScore, aiScore)
    }
}