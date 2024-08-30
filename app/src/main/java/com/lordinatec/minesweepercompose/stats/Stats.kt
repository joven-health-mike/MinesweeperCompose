/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

import android.content.Context

class Stats {
    companion object {
        private const val PREFS_NAME = "MinesweeperComposePrefs"
        private const val PREFS_BEST_TIME = "bestTime"
        private const val PREFS_WINS = "wins"
        private const val PREFS_LOSSES = "losses"

        fun getBestTime(context: Context): Long {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getLong(PREFS_BEST_TIME, 0)
        }

        fun setBestTime(context: Context, time: Long) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putLong(PREFS_BEST_TIME, time)
            editor.apply()
        }

        fun getWins(context: Context): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getInt(PREFS_WINS, 0)
        }

        fun setWins(context: Context, wins: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putInt(PREFS_WINS, wins)
            editor.apply()
        }

        fun getLosses(context: Context): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getInt(PREFS_LOSSES, 0)
        }

        fun setLosses(context: Context, losses: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putInt(PREFS_LOSSES, losses)
            editor.apply()
        }
    }
}