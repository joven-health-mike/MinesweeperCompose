/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.android

import android.content.Context
import android.content.SharedPreferences
import com.lordinatec.minesweepercompose.android.SharedPreferencesDelegate
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlin.reflect.KProperty
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedPreferencesDelegateTest {
    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var sharedPreferences: SharedPreferences

    @MockK
    private lateinit var editor: SharedPreferences.Editor

    @MockK
    private lateinit var property: KProperty<Int>

    private lateinit var sharedPreferencesDelegate: SharedPreferencesDelegate

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        every { context.getSharedPreferences(any(), any()) } answers { sharedPreferences }
        every { sharedPreferences.edit() } answers { editor }
        every { editor.putString(any(), any()) } answers { editor }
        every { editor.apply() } just Runs
        sharedPreferencesDelegate = SharedPreferencesDelegate(context, "test")
    }

    @Test
    fun testSet() {
        sharedPreferencesDelegate.setValue(null, property, "3.14159")
        verify { editor.putString("test", "3.14159") }
    }

    @Test
    fun testGet() {
        every { sharedPreferences.getString("test", "") } returns "3.14159"
        val value = sharedPreferencesDelegate.getValue(null, property)
        verify { sharedPreferences.getString("test", any()) }
        assertEquals("3.14159", value)
    }
}
