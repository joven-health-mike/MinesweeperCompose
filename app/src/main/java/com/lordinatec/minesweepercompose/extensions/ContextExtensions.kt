/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.extensions

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

/**
 * Get the [ComponentActivity] from a [Context] if available.
 *
 * @return the [ComponentActivity] if available, otherwise null.
 */
fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}