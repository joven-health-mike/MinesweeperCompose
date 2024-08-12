package com.lordinatec.minesweepercompose.extensions

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

// extension function to get an activity from a context
fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}