package com.lordinatec.minesweepercompose

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lordinatec.minesweepercompose.minesweeper.apis.view.GameView
import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme

class GameActivity : ComponentActivity() {
    private class LifecycleObserver(val gameViewModel: GameViewModel) : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            gameViewModel.resumeTimer()
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            gameViewModel.pauseTimer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel by viewModels()
            val lifecycleObserver = LifecycleObserver(viewModel)
            lifecycle.addObserver(lifecycleObserver);
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Modifier.padding(innerPadding)
                    GameView()
                }
            }
        }
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, GameActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }
}