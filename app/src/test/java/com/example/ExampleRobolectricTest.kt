package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.VideoItem
import com.example.player.AspectRatioMode
import com.example.ui.theme.AppThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/*
 * Copyright (c) 2026, Sam Moreno.
 * Licensed under the BSD 2-Clause License.
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SamVideo", appName)
    }

    @Test
    fun `video item duration and resolution formatting`() {
        val item = VideoItem(
            id = 1L,
            uri = android.net.Uri.parse("file:///dummy.mp4"),
            title = "Test Video",
            durationMs = 125000L, // 2 minutes and 5 seconds
            sizeBytes = 10485760L, // 10 MB
            width = 1920,
            height = 1080
        )

        assertEquals("02:05", item.formattedDuration)
        assertEquals("10.0 MB", item.formattedSize)
        assertEquals("1080p", item.resolutionLabel)
    }

    @Test
    fun `aspect ratio mode cycling`() {
        val fit = AspectRatioMode.FIT
        val zoom = fit.next()
        val fill = zoom.next()
        val backToFit = fill.next()

        assertEquals(AspectRatioMode.ZOOM, zoom)
        assertEquals(AspectRatioMode.FILL, fill)
        assertEquals(AspectRatioMode.FIT, backToFit)
    }

    @Test
    fun `theme modes titles and availability`() {
        val modes = AppThemeMode.entries
        assertEquals(3, modes.size)
        assertEquals("Escuro", AppThemeMode.DARK.title)
        assertEquals("Claro", AppThemeMode.LIGHT.title)
        assertEquals("Azul Sóbrio", AppThemeMode.SLATE_BLUE.title)
    }
}
