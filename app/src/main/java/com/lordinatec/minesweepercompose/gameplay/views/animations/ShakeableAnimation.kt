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
 * A class that can be used to shake a view.
 */
class ShakeableAnimation {
    /**
     * The current x position of the view.
     */
    val xPosition = Animatable(0f)
    private val animationSpec: AnimationSpec<Float> = tween(durationMillis = 20)

    /**
     * Shake the view.
     */
    suspend fun shake() {
        // move left, center, right, center 3x
        repeat(3) {
            xPosition.animateTo(
                targetValue = -40f,
                animationSpec = animationSpec
            )
            xPosition.animateTo(
                targetValue = 0f,
                animationSpec = animationSpec
            )
            xPosition.animateTo(
                targetValue = 40f,
                animationSpec = animationSpec
            )
            xPosition.animateTo(
                targetValue = 0f,
                animationSpec = animationSpec
            )
        }
    }
}

/**
 * A composable that creates & remembers a [ShakeableAnimation] instance.
 */
@Composable
fun rememberShakeable(): ShakeableAnimation {
    return remember { ShakeableAnimation() }
}

/**
 * Modifier extension function that applies the x translation to the view.
 */
fun Modifier.shakeable(shakeable: ShakeableAnimation): Modifier {
    return then(graphicsLayer(translationX = shakeable.xPosition.value))
}
