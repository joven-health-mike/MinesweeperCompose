/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.GameActivity
import com.lordinatec.minesweepercompose.stats.StatsActivity
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme
import com.lordinatec.minesweepercompose.views.AppIcon
import kotlin.math.floor

/**
 * The main activity for the Minesweeper game.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        determineFieldSize()
        enableEdgeToEdge()
        setContent {
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val isPortraitMode =
                        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
                    if (isPortraitMode) {
                        PortraitScreen(modifier = Modifier.padding(innerPadding))
                    } else {
                        LandscapeScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }

    private fun determineFieldSize() {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val tileSize = resources.getDimension(R.dimen.tile_view_size)
        val desiredFieldWidth = floor(screenWidth * 0.9f / tileSize).toInt()
        val desiredFieldHeight = floor(screenHeight * 0.8f / tileSize).toInt()
        Config.width = desiredFieldWidth
        Config.height = desiredFieldHeight
        Config.mines = desiredFieldWidth * desiredFieldHeight / 6
    }

    @Composable
    private fun PortraitScreen(modifier: Modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AppIcon(dimensionResource(id = R.dimen.main_activity_icon_size))
                MenuButtons()
            }
        }
    }

    @Composable
    private fun LandscapeScreen(modifier: Modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppIcon(dimensionResource(id = R.dimen.main_activity_icon_size))
                MenuButtons()
            }
        }
    }

    @Composable
    private fun MenuButtons() {
        val buttonWidth = dimensionResource(id = R.dimen.main_activity_button_width)
        Column(
            modifier = Modifier.width(buttonWidth)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { GameActivity.launch(applicationContext) }) {
                Text("New Game")
            }
            // TODO: make stats activity a NavHost destination instead of new activity
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { StatsActivity.launch(applicationContext) }) {
                Text("Stats")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { finish() }) {
                Text("Exit")
            }
        }
    }
}
