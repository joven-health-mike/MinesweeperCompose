package com.lordinatec.minesweepercompose

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.view.FieldView
import com.lordinatec.minesweepercompose.minesweeper.apis.view.FieldViewListener
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme

class GameActivity : ComponentActivity() {
    val activity = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Modifier.padding(innerPadding)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .width(50.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        FieldView(FieldViewModel(), object : FieldViewListener {
                            override fun onExitClicked() {
                                activity.finish()
                            }
                        })
                    }
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