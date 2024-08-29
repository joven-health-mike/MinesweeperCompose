/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity

class StatsActivity : ComponentActivity() {
    private val activityContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                val wins = Stats.getWins(activityContext)
                val losses = Stats.getLosses(activityContext)
                Spacer(modifier = Modifier.fillMaxSize(0.1f))
                Text("Stats")
                Text(
                    "Best Time: ${
                        formatFloat(
                            Stats.getBestTime(activityContext) / 1000f,
                            3
                        )
                    } seconds"
                )
                Text("Wins: $wins")
                Text("Losses: $losses")
                Text(
                    "Win Rate: ${
                        if (wins + losses == 0) "0.0" else formatFloat(
                            wins.toFloat() * 100 / (wins + losses),
                            1
                        )
                    }%"
                )
            }
        }
    }

    private fun formatFloat(value: Float, padding: Int): String {
        val intValue = value.toInt()
        var decValue = (value - intValue).toString().padEnd(padding, '0')
        val maxLength = 2 + padding
        decValue =
            decValue.substring(2, if (decValue.length > maxLength) maxLength else decValue.length)
        return "$intValue.$decValue"
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, StatsActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }
}