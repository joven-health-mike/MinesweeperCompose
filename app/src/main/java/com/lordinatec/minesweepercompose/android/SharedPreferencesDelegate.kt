/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.android

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate for reading and writing to shared preferences.
 *
 * @param context The context.
 * @param name The name of the shared preference.
 * @param defaultValue The default value.
 */
class SharedPreferencesDelegate(
    private val context: Context,
    private val name: String,
    private val defaultValue: String = ""
) : ReadWriteProperty<Any?, String> {
    private val sharedPreferences by lazy {
        context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return sharedPreferences.getString(name, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        sharedPreferences.edit().putString(name, value).apply()
    }
}

/**
 * Extension function for getting shared preferences.
 *
 * @param name The name of the shared preference.
 *
 * @return The shared preference delegate.
 */
fun Context.sharedPreferences(name: String, defaultValue: String = "") =
    SharedPreferencesDelegate(this, name, defaultValue)
