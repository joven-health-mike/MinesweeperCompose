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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.lordinatec.minesweepercompose.gameplay.GameActivity
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.logger.LogcatLogger
import com.lordinatec.minesweepercompose.settings.SettingsActivity
import com.lordinatec.minesweepercompose.stats.StatsActivity
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme
import com.lordinatec.minesweepercompose.views.AppIcon
import com.lordinatec.minesweepercompose.views.TopBar
import com.lordinatec.minesweepercompose.views.topBarPadding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The main activity for the Minesweeper game.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var logcatLogger: LogcatLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperComposeTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = { TopBar(resources.getString(R.string.app_name)) }) { innerPadding ->
                    Modifier.padding(innerPadding)
                    val isPortraitMode =
                        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            logcatLogger.consume()
                        }
                    }

                    if (isPortraitMode) {
                        PortraitScreen()
                    } else {
                        LandscapeScreen()
                    }
                }
            }
        }
    }

    @Composable
    private fun PortraitScreen() {
        val systemBarBottom = WindowInsets.systemBars.getBottom(Density(this)).dp
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .statusBarsPadding()
                    .topBarPadding(systemBarBottom),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AppIcon(dimensionResource(id = R.dimen.main_activity_icon_size))
                MenuButtons()
            }
        }
    }

    @Composable
    private fun LandscapeScreen() {
        val systemBarBottom = WindowInsets.systemBars.getBottom(Density(this)).dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .topBarPadding(systemBarBottom)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
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
        val viewModel: GameViewModel = hiltViewModel()
        val buttonWidth = dimensionResource(id = R.dimen.main_activity_button_width)
        Column(
            modifier = Modifier.width(buttonWidth)
        ) {
            Button(modifier = Modifier.fillMaxWidth(),
                onClick = {
                    GameActivity.launch(applicationContext)
                    viewModel.resetGame()
                }) {
                Text("New Game")
            }
            // TODO: make stats activity a NavHost destination instead of new activity
            Button(modifier = Modifier.fillMaxWidth(),
                onClick = { StatsActivity.launch(applicationContext) }) {
                Text("Stats")
            }
            Button(modifier = Modifier.fillMaxWidth(),
                onClick = { SettingsActivity.launch(applicationContext) }) {
                Text("Settings")
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = { finish() }) {
                Text("Exit")
            }
        }
    }
}
