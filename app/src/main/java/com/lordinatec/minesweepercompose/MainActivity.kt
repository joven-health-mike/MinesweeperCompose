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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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

    @Composable
    fun PortraitScreen(modifier: Modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AppIcon()
                MenuButtons()
            }
        }
    }

    @Composable
    fun LandscapeScreen(modifier: Modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppIcon()
                MenuButtons()
            }
        }
    }

    @Composable
    private fun MenuButtons() {
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

    @Composable
    private fun AppIcon() {
        Icon(
            painterResource(R.drawable.android_icon_foreground_minesweeper),
            "app icon",
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        )
    }
}
