package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import android.content.SharedPreferences
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil.PREF_FILE_NAME
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil.PREF_KEY_LAUNCH_TIMES
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PreferenceUtilTest {
    @MockK
    lateinit var context: Context
    @MockK
    lateinit var sharedPreferences: SharedPreferences
    @RelaxedMockK
    lateinit var editor: SharedPreferences.Editor

    @BeforeEach
    fun setup() {
        RatingLogger.isLoggingEnabled = false
        mockkObject(PreferenceUtil)
        every { context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
    }

    @Test
    fun `correct preferences are used`() {
        assertEquals(PreferenceUtil.getPreferences(context), sharedPreferences)
    }

    @Test
    fun `increase launch times works correctly`() {
        every { PreferenceUtil.getLaunchTimes(context) } returns 0
        PreferenceUtil.increaseLaunchTimes(context)
        verify(exactly = 1) { editor.putInt(PREF_KEY_LAUNCH_TIMES, 1) }
    }

    @Test
    fun `reset works correctly`() {
        PreferenceUtil.reset(context)
        verify(exactly = 1) { editor.clear() }
    }
}
