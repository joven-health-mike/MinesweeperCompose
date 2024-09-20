/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.views.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * A class that can be move a view from up to it's intended position.
 */
class DropDownAnimation(startPosition: Float) {
    /**
     * The current y position of the view.
     */
    val yPosition = Animatable(startPosition)
    private val animationSpec: AnimationSpec<Float> = tween(durationMillis = 20)

    /**
     * Move the view from up to it's intended position.
     */
    suspend fun drop() {
        yPosition.animateTo(
            targetValue = 0f,
            animationSpec = animationSpec
        )
    }
}

/**
 * A composable that creates & remembers a [DropDownAnimation] instance.
 */
@Composable
fun rememberDropDownAnimation(startPosition: Float): DropDownAnimation {
    return remember { DropDownAnimation(startPosition) }
}

/**
 * Modifier extension function that applies the y translation to the view.
 */
fun Modifier.dropDown(dropDownAnimation: DropDownAnimation): Modifier {
    return graphicsLayer(translationY = dropDownAnimation.yPosition.value)
}
