package com.lordinatec.minesweepercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(200.dp))
                                Icon(
                                    painterResource(R.drawable.android_icon_foreground_minesweeper),
                                    "app icon",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .height(200.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            Column {
                                Button(
                                    modifier = Modifier.width(250.dp),
                                    onClick = { GameActivity.launch(applicationContext) }) {
                                    Text("New Game")
                                }
                                Button(
                                    modifier = Modifier.width(250.dp),
                                    onClick = { finish() }) {
                                    Text("Exit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}