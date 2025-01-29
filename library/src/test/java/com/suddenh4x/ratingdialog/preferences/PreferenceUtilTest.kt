package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import android.content.SharedPreferences
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil.PREF_FILE_NAME
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil.PREF_KEY_LAUNCH_TIMES
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil.PREF_KEY_NUMBER_OF_LATER_BUTTON_CLICKS
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil.PREF_KEY_REMIND_TIMESTAMP
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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
        every {
            context.getSharedPreferences(
                PREF_FILE_NAME,
                Context.MODE_PRIVATE,
            )
        } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { context.getString(any()) } returns ""
        every { context.getString(any(), any()) } returns ""
    }

    @AfterEach
    fun shutdown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `correct preferences are used`() {
        assertEquals(PreferenceUtil.getPreferences(context), sharedPreferences)
    }

    @Test
    fun `increase launch times works correctly`() {
        every { PreferenceUtil.getLaunchTimes(context) } returns 0 andThen 1
        PreferenceUtil.increaseLaunchTimes(context)
        PreferenceUtil.increaseLaunchTimes(context)
        verify(exactly = 1) { editor.putInt(PREF_KEY_LAUNCH_TIMES, 1) }
        verify(exactly = 1) { editor.putInt(PREF_KEY_LAUNCH_TIMES, 2) }
    }

    @Test
    fun `increase number of later button clicks works correctly`() {
        every { PreferenceUtil.getNumberOfLaterButtonClicks(context) } returns 0 andThen 1
        PreferenceUtil.increaseNumberOfLaterButtonClicks(context)
        PreferenceUtil.increaseNumberOfLaterButtonClicks(context)
        verify(exactly = 1) { editor.putInt(PREF_KEY_NUMBER_OF_LATER_BUTTON_CLICKS, 1) }
        verify(exactly = 1) { editor.putInt(PREF_KEY_NUMBER_OF_LATER_BUTTON_CLICKS, 2) }
    }

    // fixme: The System class can't be mocked at the moment: https://github.com/mockk/mockk/issues/98
    @Test
    @Disabled
    fun `get remind timestamp returns current time if not set`() {
        mockkStatic(System::class)
        every { sharedPreferences.getLong(any(), any()) } returns -1L
        every { System.currentTimeMillis() } returns CURRENT_TIME_IN_MILLIS

        PreferenceUtil.getRemindTimestamp(context)

        verify(exactly = 1) {
            sharedPreferences.getLong(
                PREF_KEY_REMIND_TIMESTAMP,
                CURRENT_TIME_IN_MILLIS,
            )
        }
    }

    @Test
    fun `reset works correctly`() {
        PreferenceUtil.reset(context)
        verify(exactly = 1) { editor.clear() }
    }

    companion object {

        private const val CURRENT_TIME_IN_MILLIS = 123456789L
    }
}
