/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.lordinatec.minesweepercompose.R
import com.lordinatec.minesweepercompose.android.sharedPreferences
import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.timer.TimerLifecycleObserver
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.gameplay.views.GameView
import com.lordinatec.minesweepercompose.stats.StatsEventConsumer
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.floor

/**
 * The main activity for gameplay.
 */
@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    @Inject
    lateinit var statsEventConsumer: StatsEventConsumer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isPortraitMode =
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        loadConfigFromPrefs()
        if (Config.feature_adjust_field_to_screen_size) {
            determineFieldSize(isPortraitMode)
        }
        setContent {
            val viewModel: GameViewModel = hiltViewModel()
            lifecycle.addObserver(TimerLifecycleObserver(viewModel))
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            statsEventConsumer.consume()
                        }
                    }
                    Modifier.padding(innerPadding)
                    GameView(viewModel)
                }
            }
        }
    }

    private fun loadConfigFromPrefs() {
        val adjustFieldSizeToScreenSize by sharedPreferences(
            "adjustToScreenSize",
            Config.feature_adjust_field_to_screen_size.toString()
        )
        Config.feature_adjust_field_to_screen_size = adjustFieldSizeToScreenSize.toBoolean()
        val endGameOnLastFlag by sharedPreferences(
            "endGameOnLastFlag",
            Config.feature_end_game_on_last_flag.toString()
        )
        Config.feature_end_game_on_last_flag = endGameOnLastFlag.toBoolean()
    }

    private fun determineFieldSize(isPortraitMode: Boolean) {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val tileSize = resources.getDimension(R.dimen.tile_view_size)
        val fieldWidthPadding = if (isPortraitMode) 0.9f else 0.75f
        val fieldHeightPadding = if (isPortraitMode) 0.8f else 0.9f
        val desiredFieldWidth = floor(screenWidth * fieldWidthPadding / tileSize).toInt()
        val desiredFieldHeight = floor(screenHeight * fieldHeightPadding / tileSize).toInt()
        Config.width = if (isPortraitMode) desiredFieldWidth else desiredFieldHeight
        Config.height = if (isPortraitMode) desiredFieldHeight else desiredFieldWidth
        Config.mines = desiredFieldWidth * desiredFieldHeight / 6
    }

    companion object {
        /**
         * Launch the game activity as a new task.
         *
         * @param context The context to launch the activity from
         */
        fun launch(context: Context) {
            val intent = Intent(context, GameActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }
}
