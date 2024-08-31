/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

fun interface TimeProvider {
    fun currentMillis(): Long
}