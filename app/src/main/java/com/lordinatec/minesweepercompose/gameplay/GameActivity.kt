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
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.timer.TimerFactory
import com.lordinatec.minesweepercompose.gameplay.timer.TimerLifecycleObserver
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModelFactory
import com.lordinatec.minesweepercompose.gameplay.views.GameView
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme
import kotlinx.coroutines.MainScope


class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = createCustomViewModel()
            val lifecycleObserver = TimerLifecycleObserver(viewModel)
            lifecycle.addObserver(lifecycleObserver)
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Modifier.padding(innerPadding)
                    GameView(viewModel)
                }
            }
        }
    }

    private fun createCustomViewModel(): GameViewModel {
        val gameEvents = GameEventPublisher(MainScope())
        return ViewModelProvider(
            this,
            GameViewModelFactory(
                application,
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