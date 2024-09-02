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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity
import com.lordinatec.minesweepercompose.extensions.formatString
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme
import com.lordinatec.minesweepercompose.ui.theme.Typography

/**
 * Activity to display the stats of the game
 */
class StatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        val statsProvider = StatsUpdater(applicationContext)
                        val wins = statsProvider.getWins()
                        val losses = statsProvider.getLosses()
                        Spacer(modifier = Modifier.fillMaxSize(0.1f))
                        Text(
                            "Stats",
                            style = Typography.bodyLarge
                        )
                        Text(
                            "Best Time: ${
                                (statsProvider.getBestTime() / 1000f).formatString(3)
                            } seconds",
                            style = Typography.labelSmall
                        )
                        Text(
                            "Wins: $wins",
                            style = Typography.labelSmall
                        )
                        Text(
                            "Losses: $losses",
                            style = Typography.labelSmall
                        )
                        Text(
                            "Win Rate: ${
                                if (wins + losses == 0) "0.0" else ((wins.toFloat() * 100 / (wins + losses)).formatString(
                                    1
                                ))
                            }%",
                            style = Typography.labelSmall
                        )
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Launches the StatsActivity as a new task.
         *
         * @param context The context to launch the activity from
         */
        fun launch(context: Context) {
            val intent = Intent(context, StatsActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }
}
