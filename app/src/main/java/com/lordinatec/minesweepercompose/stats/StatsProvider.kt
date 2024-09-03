/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

import android.content.Context
import com.lordinatec.minesweepercompose.android.sharedPreferences

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
class StatsUpdater(context: Context) : StatsProvider {
    private var bestTime by context.sharedPreferences("bestTime")
    private var wins by context.sharedPreferences("wins")
    private var losses by context.sharedPreferences("losses")

    override fun setBestTime(time: Long) {
        bestTime = time.toString()
    }

    override fun getBestTime(): Long {
        return try {
            bestTime.toLong()
        } catch (e: NumberFormatException) {
            0
        }
    }

    override fun setWins(wins: Int) {
        this.wins = wins.toString()
    }

    override fun getWins(): Int {
        return try {
            wins.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    override fun setLosses(losses: Int) {
        this.losses = losses.toString()
    }

    override fun getLosses(): Int {
        return try {
            losses.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}
