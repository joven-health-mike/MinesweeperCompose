/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.lordinatec.minesweepercompose.stats.Stats
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.timer.TimerFactory
import com.lordinatec.minesweepercompose.gameplay.views.GameView
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModelFactory
import com.lordinatec.minesweepercompose.gameplay.timer.TimerLifecycleObserver
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme


class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = createCustomViewModel()
            val gameState by viewModel.uiState.collectAsState()
            val lifecycleObserver = TimerLifecycleObserver(viewModel)
            lifecycle.addObserver(lifecycleObserver)
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Modifier.padding(innerPadding)
                    if (gameState.gameOver && gameState.winner) {
                        maybeSaveBestTime(this, gameState.timeValue)
                        saveWin(this)
                    } else if (gameState.gameOver) {
                        saveLoss(this)
                    }
                    GameView(viewModel)
                }
            }
        }
    }

    private fun saveLoss(context: Context) {
        val losses = Stats.getLosses(context)
        Stats.setLosses(context, losses + 1)
    }

    private fun saveWin(context: Context) {
        val wins = Stats.getWins(context)
        Stats.setWins(context, wins + 1)
    }

    private fun maybeSaveBestTime(context: Context, timeValue: Long) {
        val bestTime = Stats.getBestTime(context)
        if (timeValue < bestTime || bestTime == 0L) {
            Stats.setBestTime(context, timeValue)
        }
    }


    private fun createCustomViewModel(): GameViewModel {
        val gameEvents = GameEventPublisher(lifecycleScope)
        return ViewModelProvider(
            this,
            GameViewModelFactory(
                GameController.Factory(GameFactory(), TimerFactory())
                    .createGameController(gameEvents), gameEvents
            )
        )[GameViewModel::class.java]
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, GameActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }
}