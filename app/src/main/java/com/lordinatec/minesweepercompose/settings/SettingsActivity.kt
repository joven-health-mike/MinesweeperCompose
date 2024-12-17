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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.lordinatec.minesweepercompose.android.sharedPreferences
import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme
import com.lordinatec.minesweepercompose.ui.theme.Typography
import com.lordinatec.minesweepercompose.views.BoundedTextInput
import com.lordinatec.minesweepercompose.views.TopBar
import com.lordinatec.minesweepercompose.views.topBarPadding

/**
 * The settings activity for the Minesweeper app.
 */
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperComposeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar("Settings") }) { innerPadding ->
                    Modifier.padding(innerPadding)
                    val systemBarBottom = WindowInsets.systemBars.getBottom(Density(this)).dp
                    var showCustomField by remember { mutableStateOf(!Config.feature_adjust_field_to_screen_size) }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .topBarPadding(systemBarBottom),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        AdjustToScreenSizeRow() {
                            showCustomField = !it
                        }
                        if (showCustomField) {
                            CustomFieldSizeRow()
                        }
                        EndGameOnLastFlagRow()
                    }
                }
            }
        }
    }

    @Composable
    private fun AdjustToScreenSizeRow(onChange: (Boolean) -> Unit = {}) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var adjustToScreenSizePref by sharedPreferences(
                "adjustToScreenSize",
                Config.feature_adjust_field_to_screen_size.toString()
            )
            var width by sharedPreferences(
                "width",
                Config.width.toString()
            )
            var height by sharedPreferences(
                "height",
                Config.height.toString()
            )
            var mines by sharedPreferences(
                "mines",
                Config.mines.toString()
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
            Switch(checked = adjustToScreenSizeState, onCheckedChange = {
                adjustToScreenSizeState = it
                adjustToScreenSizePref = it.toString()
                Config.feature_adjust_field_to_screen_size = it
                onChange(it)
                if (!it) {
                    Config.factoryResetFieldConfig()
                    width = Config.width.toString()
                    height = Config.height.toString()
                    mines = Config.mines.toString()
                }
            })
        }
    }

    @Composable
    private fun CustomFieldSizeRow() {
        var widthPref by sharedPreferences(
            "width",
            Config.width.toString()
        )
        var heightPref by sharedPreferences(
            "height",
            Config.height.toString()
        )
        var minesPref by sharedPreferences(
            "mines",
            Config.mines.toString()
        )
        var widthValue by remember { mutableIntStateOf(widthPref.toInt()) }
        var heightValue by remember { mutableIntStateOf(heightPref.toInt()) }
        var minesValue by remember { mutableIntStateOf(minesPref.toInt()) }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Custom field size", style = Typography.labelSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BoundedTextInput(range = 3..20, initialValue = widthValue, label = "Width") {
                    widthValue = it
                    widthPref = it.toString()
                    Config.width = it
                    if (minesValue > widthValue * heightValue - 1) {
                        minesValue = widthValue * heightValue - 1
                        minesPref = minesValue.toString()
                        Config.mines = minesValue
                    }
                }
                BoundedTextInput(range = 3..20, initialValue = heightValue, label = "Height") {
                    heightValue = it
                    heightPref = it.toString()
                    Config.height = it
                    if (minesValue > widthValue * heightValue - 1) {
                        minesValue = widthValue * heightValue - 1
                        minesPref = minesValue.toString()
                        Config.mines = minesValue
                    }
                }
                BoundedTextInput(
                    range = 3 until widthValue * heightValue,
                    initialValue = minesValue,
                    label = "Mines"
                ) {
                    minesValue = it
                    minesPref = it.toString()
                    Config.mines = it
                }
            }
        }
    }

    @Composable
    fun EndGameOnLastFlagRow() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var endGameOnLastFlagPref by sharedPreferences(
                "endGameOnLastFlag", Config.feature_end_game_on_last_flag.toString()
            )
            var endGameOnLastFlagState by remember {
                mutableStateOf(
                    endGameOnLastFlagPref.toBoolean()
                )
            }
            Text(
                modifier = Modifier.padding(8.dp),
                text = "End game on last flag",
                style = Typography.labelSmall
            )
            Switch(checked = endGameOnLastFlagState, onCheckedChange = {
                endGameOnLastFlagState = it
                endGameOnLastFlagPref = it.toString()
                Config.feature_end_game_on_last_flag = it
            })
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
