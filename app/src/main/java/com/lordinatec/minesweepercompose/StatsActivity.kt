/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose

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
                Spacer(modifier = Modifier.fillMaxSize(0.1f))
                Text("Stats")
                Text("Best Time: ${formatTime(Stats.getBestTime(activityContext))} seconds")
            }
        }
    }

    private fun formatTime(time: Long): String {
        val floatTime = time.toFloat() / 1000
        val timeInt = floatTime.toInt()
        var timeDec = (floatTime - timeInt).toString().padEnd(3, '0')
        timeDec = timeDec.substring(2, if (timeDec.length > 5) 5 else timeDec.length)
        return "$timeInt.$timeDec"
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, StatsActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }
}