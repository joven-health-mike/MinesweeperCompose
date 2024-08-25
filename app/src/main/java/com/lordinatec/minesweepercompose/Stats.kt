/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose

import android.content.Context

class Stats {
    companion object {
        private const val PREFS_NAME = "MinesweeperComposePrefs"
        private const val PREFS_BEST_TIME = "bestTime"

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
    }
}