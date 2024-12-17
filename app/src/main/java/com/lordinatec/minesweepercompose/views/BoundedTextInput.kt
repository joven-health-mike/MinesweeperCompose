/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lordinatec.minesweepercompose.ui.theme.Typography

@Composable
fun BoundedTextInput(
    range: IntRange,
    initialValue: Int,
    label: String,
    onValueChange: (Int) -> Unit
) {
    var value by remember { mutableIntStateOf(initialValue) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .width(100.dp)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(.85f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString()
                )
            }
            Column(
                modifier = Modifier
                    .height(42.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier.clickable {
                        if (value + 1 in range) {
                            value++
                            onValueChange(value)
                        }
                    },
                    painter = painterResource(id = android.R.drawable.arrow_up_float),
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier.clickable {
                        if (value - 1 in range) {
                            value--
                            onValueChange(value)
                        }
                    },
                    painter = painterResource(id = android.R.drawable.arrow_down_float),
                    contentDescription = null
                )
            }
        }
        Text(
            text = label,
            style = Typography.labelSmall
        )
    }
}
