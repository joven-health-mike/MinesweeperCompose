package com.lordinatec.minesweepercompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lordinatec.minesweepercompose.ui.theme.MinesweeperComposeTheme

class MainActivity : ComponentActivity() {
    private val fieldWidth = 5
    private val fieldHeight = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Field(
                        fieldWidth,
                        fieldHeight,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Field(width: Int, height: Int, modifier: Modifier = Modifier) {
    val lightGray = Color(0xFF454545)
    val darkGray = Color(0xFFA5A5A5)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            for (i in 0..<height) {
                Row {
                    for (j in 0..<width) {
                        val location = Location(j, i)
                        CoveredTile(location, lightGray, darkGray, modifier)
                    }
                }
            }
        }
    }

}

fun onItemClick(location: Location) {
    Log.d("MIKE_BURKE", "Item clicked [$location.x, $location.y]")
}

@Composable
fun CoveredTile(
    location: Location,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .clickable {
                onItemClick(location)
            }
            .size(75.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor,
                        secondaryColor
                    )
                )
            )
    )
}