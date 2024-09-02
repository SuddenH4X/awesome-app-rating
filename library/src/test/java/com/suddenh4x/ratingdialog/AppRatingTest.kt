package com.suddenh4x.ratingdialog

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.suddenh4x.ratingdialog.dialog.DialogOptions
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.ConditionsChecker
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.utils.FeedbackUtils
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AppRatingTest {

    @MockK
    lateinit var activity: AppCompatActivity
    internal lateinit var dialogOptions: DialogOptions

    @BeforeEach
    fun setup() {
        RatingLogger.isLoggingEnabled = false
        dialogOptions = DialogOptions()
        unmockkAll()
        every { activity.getString(any()) } returns ""
        every { activity.getString(any(), any()) } returns ""
    }

    @Test
    fun `mail settings are set correctly into dialogOptions`() {
        getBuilder().setMailSettingsForFeedbackDialog(mailSettings)
        assertThat(dialogOptions.mailSettings).isEqualTo(mailSettings)
    }

    @Test
    fun `custom condition is set correctly into dialogOptions`() {
        getBuilder().setCustomCondition(customCondition)
        assertThat(dialogOptions.customCondition?.invoke()).isEqualTo(customCondition())
    }

    @Test
    fun `custom condition to show again is set correctly into dialogOptions`() {
        getBuilder().setCustomConditionToShowAgain(customCondition)
        assertThat(dialogOptions.customConditionToShowAgain?.invoke()).isEqualTo(customCondition())
    }

    @Test
    fun `countAppLaunch is set correctly into dialogOptions`() {
        assertThat(dialogOptions.countAppLaunch).isTrue()
        getBuilder().dontCountThisAsAppLaunch()
        assertThat(dialogOptions.countAppLaunch).isFalse()
    }

    @Nested
    inner class PreferenceUtilCalls {
        @MockK(relaxed = true)
        lateinit var sharedPreferences: SharedPreferences

        @BeforeEach
        fun setup() {
            mockkObject(PreferenceUtil)
            every { PreferenceUtil.getPreferences(activity) } returns sharedPreferences
        }

        @Test
        fun `reset triggers reset of PreferenceUtil`() {
            AppRating.reset(activity)
            verify { PreferenceUtil.reset(activity) }
        }

        @Test
        fun `minimum launch times is set correctly into PreferenceUtil`() {
            getBuilder().setMinimumLaunchTimes(10)
            verify { PreferenceUtil.setMinimumLaunchTimes(activity, 10) }
        }

        @Test
        fun `minimum launch times to show again is set correctly into PreferenceUtil`() {
            getBuilder().setMinimumDaysToShowAgain(10)
            verify { PreferenceUtil.setMinimumDaysToShowAgain(activity, 10) }
        }

        @Test
        fun `minimum days is set correctly into PreferenceUtil`() {
            getBuilder().setMinimumDays(10)
            verify { PreferenceUtil.setMinimumDays(activity, 10) }
        }

        @Test
        fun `minimum days to show again is set correctly into PreferenceUtil`() {
            getBuilder().setMinimumDaysToShowAgain(10)
            verify { PreferenceUtil.setMinimumDaysToShowAgain(activity, 10) }
        }

        @Test
        fun `was later button clicked calls correct method of PreferenceUtil`() {
            AppRating.wasLaterButtonClicked(activity)
            verify { PreferenceUtil.wasLaterButtonClicked(activity) }
        }
    }

    @Test
    fun `logging enabled is set correctly into RatingLogger`() {
        getBuilder().setLoggingEnabled(false)
        assertThat(RatingLogger.isLoggingEnabled).isEqualTo(false)
    }

    @Test
    fun `debug is set correctly into RatingLogger`() {
        val builder = getBuilder()
        builder.setDebug(true)
        assertThat(builder.isDebug).isEqualTo(true)
    }

    @Test
    fun `google in-app review complete listener is set correctly into dialogOptions`() {
        val completeListener: (Boolean) -> Unit = mockk()
        getBuilder().setGoogleInAppReviewCompleteListener(completeListener)
        assertThat(dialogOptions.googleInAppReviewCompleteListener).isEqualTo(completeListener)
    }

    @Nested
    inner class ShowNow {

        private lateinit var appRatingBuilder: AppRating.Builder

        @BeforeEach
        fun setup() {
            appRatingBuilder = spyk(getBuilder())
        }

        @Test
        fun `calls showGoogleInAppReview if useGoogleInAppReview is true`() {
            every { appRatingBuilder.showGoogleInAppReview() } just Runs

            appRatingBuilder.showNow()

            verify(exactly = 1) { appRatingBuilder.showGoogleInAppReview() }
        }
    }

    @Nested
    inner class ShowIfMeetsConditions {
        private val TAG = "AwesomeAppRatingDialog"

        @BeforeEach
        fun setup() {
            mockkObject(PreferenceUtil)
            mockkObject(ConditionsChecker)
            every { PreferenceUtil.increaseLaunchTimes(any()) } just Runs
            every { ConditionsChecker.shouldShowDialog(activity, dialogOptions) } returns false
            every {
                activity.supportFragmentManager.findFragmentByTag(TAG)
            } returns null
        }

        @Test
        fun `returns immediately false if dialog is currently visible`() {
            val appRatingBuilder = spyk(getBuilder())
            every {
                activity.supportFragmentManager.findFragmentByTag(TAG)
            } returns mockk()

            val result = getBuilder().showIfMeetsConditions()

            verify(exactly = 0) { appRatingBuilder.showNow() }
            assertThat(result).isFalse
        }

        @Test
        fun `increases launch times`() {
            getBuilder().showIfMeetsConditions()

            verify(exactly = 1) { PreferenceUtil.increaseLaunchTimes(activity) }
        }

        @Test
        fun `doesn't increase launch times if countAppLaunch is false`() {
            dialogOptions.countAppLaunch = false

            getBuilder().showIfMeetsConditions()

            verify(exactly = 0) { PreferenceUtil.increaseLaunchTimes(activity) }
        }

        @Test
        fun `calls show now if conditions are met`() {
            val appRatingBuilder = spyk(getBuilder())
            every { ConditionsChecker.shouldShowDialog(activity, dialogOptions) } returns true
            every { appRatingBuilder.showNow() } just Runs

            appRatingBuilder.showIfMeetsConditions()

            verify(exactly = 1) { appRatingBuilder.showNow() }
        }

        @Test
        fun `returns true if conditions are met`() {
            val appRatingBuilder = spyk(getBuilder())
            every { ConditionsChecker.shouldShowDialog(activity, dialogOptions) } returns true
            every { appRatingBuilder.showNow() } just Runs

            val result = appRatingBuilder.showIfMeetsConditions()

            assertThat(result).isTrue
        }

        @Test
        fun `calls show now if isDebug is true`() {
            val appRatingBuilder = spyk(getBuilder()).apply { isDebug = true }
            every { appRatingBuilder.showNow() } just Runs

            appRatingBuilder.showIfMeetsConditions()

            verify(exactly = 1) { appRatingBuilder.showNow() }
        }

        @Test
        fun `doesn't call show now if conditions aren't met`() {
            val appRatingBuilder = spyk(getBuilder())

            appRatingBuilder.showIfMeetsConditions()

            verify(exactly = 0) { appRatingBuilder.showNow() }
        }

        @Test
        fun `returns false if conditions aren't met`() {
            val appRatingBuilder = spyk(getBuilder())

            val result = appRatingBuilder.showIfMeetsConditions()

            assertThat(result).isFalse
        }
    }

    @Test
    fun `open mail feedback calls correct function of FeedbackUtils`() {
        mockkObject(FeedbackUtils)
        every { FeedbackUtils.openMailFeedback(any(), any()) } just Runs
        val context = mockk<Context>()
        AppRating.openMailFeedback(context, mailSettings)
        verify(exactly = 1) { FeedbackUtils.openMailFeedback(context, mailSettings) }
    }

    @Test
    fun `open play store listing calls correct function of FeedbackUtils`() {
        mockkObject(FeedbackUtils)
        every { FeedbackUtils.openPlayStoreListing(any()) } just Runs
        val context = mockk<Context>()
        AppRating.openPlayStoreListing(context)
        verify(exactly = 1) { FeedbackUtils.openPlayStoreListing(context) }
    }

    private fun getBuilder() = AppRating.Builder(activity, dialogOptions)

    companion object {
        private val customCondition: () -> Boolean = { true }
        private val mailSettings = MailSettings("mailAddress", "subject", "message", "errorToast")
    }
}
