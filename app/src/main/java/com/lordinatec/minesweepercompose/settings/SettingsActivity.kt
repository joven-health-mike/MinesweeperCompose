/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.settings

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.lordinatec.minesweepercompose.android.sharedPreferences
import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme
import com.lordinatec.minesweepercompose.ui.theme.Typography

class SettingsActivity : ComponentActivity() {
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
                        Spacer(modifier = Modifier.fillMaxSize(0.1f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            var adjustToScreenSizePref by sharedPreferences(
                                "adjustToScreenSize",
                                Config.feature_adjust_field_to_screen_size.toString()
                            )
                            var adjustToScreenSizeState by remember {
                                mutableStateOf(
                                    adjustToScreenSizePref.toBoolean()
                                )
                            }
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = "Adjust field size to screen size",
                                style = Typography.labelSmall
                            )
                            Switch(
                                checked = adjustToScreenSizeState,
                                onCheckedChange = {
                                    adjustToScreenSizeState = it
                                    adjustToScreenSizePref = it.toString()
                                    Config.feature_adjust_field_to_screen_size = it
                                    if (!it) {
                                        Config.factoryResetFieldConfig()
                                    }
                                })
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Launches the SettingsActivity as a new task.
         *
         * @param context The context to launch the activity from
         */
        fun launch(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }
}