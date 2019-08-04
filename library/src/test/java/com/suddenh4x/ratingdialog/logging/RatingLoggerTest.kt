package com.suddenh4x.ratingdialog.logging

import android.util.Log
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RatingLoggerTest {

    @BeforeEach
    fun setup() {
        RatingLogger.isLoggingEnabled = true
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun `correct tag is being used`() {
        RatingLogger.verbose("test")
        verify(exactly = 1) { Log.v(TAG, "test") }
    }

    @Test
    fun `verbose works correctly`() {
        RatingLogger.verbose("verbose")
        verify(exactly = 1) { Log.v(TAG, "verbose") }
    }

    @Test
    fun `debug works correctly`() {
        RatingLogger.debug("debug")
        verify(exactly = 1) { Log.d(TAG, "debug") }
    }

    @Test
    fun `info works correctly`() {
        RatingLogger.info("info")
        verify(exactly = 1) { Log.i(TAG, "info") }
    }

    @Test
    fun `warn works correctly`() {
        RatingLogger.warn("warn")
        verify(exactly = 1) { Log.w(TAG, "warn") }
    }

    @Test
    fun `error works correctly`() {
        RatingLogger.error("error")
        verify(exactly = 1) { Log.e(TAG, "error") }
    }

    @Test
    fun `disabling logger works correctly`() {
        RatingLogger.isLoggingEnabled = false
        RatingLogger.verbose("")
        RatingLogger.debug("")
        RatingLogger.info("")
        RatingLogger.warn("")
        RatingLogger.error("")

        verify(exactly = 0) { Log.d(any(), any()) }
        verify(exactly = 0) { Log.v(any(), any()) }
        verify(exactly = 0) { Log.i(any(), any()) }
        verify(exactly = 0) { Log.w(any(), any<String>()) }
        verify(exactly = 0) { Log.e(any(), any()) }
    }

    companion object {
        private const val TAG = "awesome_app_rating"
    }
}
