/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

import android.content.Context

/**
 * Interface for providing stats for the game.
 */
interface StatsProvider {
    /**
     * Set the best time for the game.
     *
     * @param time The best time in milliseconds.
     */
    fun setBestTime(time: Long)

    /**
     * Get the best time for the game.
     *
     * @return The best time in milliseconds.
     */
    fun getBestTime(): Long

    /**
     * Set the number of wins.
     *
     * @param wins The number of wins.
     */
    fun setWins(wins: Int)

    /**
     * Get the number of wins.
     *
     * @return The number of wins.
     */
    fun getWins(): Int

    /**
     * Set the number of losses.
     *
     * @param losses The number of losses.
     */
    fun setLosses(losses: Int)

    /**
     * Get the number of losses.
     *
     * @return The number of losses.
     */
    fun getLosses(): Int
}

/**
 * Implementation of [StatsProvider] that uses shared preferences to store the stats.
 */
class StatsUpdater(private val context: Context) : StatsProvider {
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
