/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

import android.content.Context

interface StatsProvider {
    fun setBestTime(time: Long)
    fun getBestTime(): Long
    fun setWins(wins: Int)
    fun getWins(): Int
    fun setLosses(losses: Int)
    fun getLosses(): Int
}

class StatsUpdater(val context: Context) : StatsProvider {
    override fun setBestTime(time: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putLong(PREFS_BEST_TIME, time)
        editor.apply()
    }

    override fun getBestTime(): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(PREFS_BEST_TIME, 0)
    }

    override fun setWins(wins: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(PREFS_WINS, wins)
        editor.apply()
    }

    override fun getWins(): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(PREFS_WINS, 0)
    }

    override fun setLosses(losses: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(PREFS_LOSSES, losses)
        editor.apply()
    }

    override fun getLosses(): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(PREFS_LOSSES, 0)
    }

    companion object {
        private const val PREFS_NAME = "MinesweeperComposePrefs"
        private const val PREFS_BEST_TIME = "bestTime"
        private const val PREFS_WINS = "wins"
        private const val PREFS_LOSSES = "losses"
    }
}