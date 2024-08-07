package com.lordinatec.minesweepercompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.lordinatec.minesweepercompose.minesweeper.api.controller.FieldController
import com.lordinatec.minesweepercompose.minesweeper.api.view.FieldView
import com.lordinatec.minesweepercompose.minesweeper.api.view.TileView
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme

class MainActivity : ComponentActivity() {
    private val fieldWidth = 5
    private val fieldHeight = 9
    private val numOfMines = 7
    private val controller =
        FieldController(fieldWidth, fieldHeight, numOfMines, ControllerListener())
    private val fieldView = FieldView(fieldWidth, fieldHeight, controller)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Modifier.padding(innerPadding)
                    fieldView.Field()
                    controller.viewUpdater = fieldView.viewUpdater!!
                }
            }
        }
    }

    private class ControllerListener : FieldController.Listener {
        override fun gameOver(win: Boolean) {
            // TODO: show Game Over dialog
            Log.d("MIKE_BURKE", "gameOver")
        }

        override fun flagAdded() {
            // TODO: make flag added sound
            Log.d("MIKE_BURKE", "flagAdded")
        }

        override fun flagRemoved() {
            // TODO: make flag removed sound
            Log.d("MIKE_BURKE", "flagRemoved")
        }

        override fun onFirstClick() {
            // TODO: start the clock
            Log.d("MIKE_BURKE", "onFirstClick")
        }
    }
}