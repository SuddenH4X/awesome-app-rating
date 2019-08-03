package com.suddenh4x.ratingdialog

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.suddenh4x.ratingdialog.buttons.CustomFeedbackButtonClickListener
import com.suddenh4x.ratingdialog.buttons.RateDialogClickListener
import com.suddenh4x.ratingdialog.dialog.DialogOptions
import com.suddenh4x.ratingdialog.dialog.RateDialogFragment
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.ConditionsChecker
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.preferences.RatingThreshold
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AppRatingTest {
    @MockK
    lateinit var activity: AppCompatActivity

    @BeforeEach
    fun setup() {
        RatingLogger.isLoggingEnabled = false
        unmockkAll()
    }

    @Test
    fun `icon drawable is set correctly into DialogOptions`() {
        val drawable = mockk<Drawable>()
        AppRating.Builder(activity).setIconDrawable(drawable)
        Assertions.assertThat(DialogOptions.iconDrawable).isEqualTo(drawable)
    }

    @Test
    fun `rate later button is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setRateLaterButton(INT_RES_ID, clickListener)
        DialogOptions.rateLaterButton.let { button ->
            Assertions.assertThat(button.textId).isEqualTo(INT_RES_ID)
            Assertions.assertThat(button.rateDialogClickListener).isEqualTo(clickListener)
        }
    }

    @Test
    fun `rate never button is set correctly into DialogOptions`() {
        AppRating.Builder(activity).showRateNeverButton(INT_RES_ID, clickListener)
        DialogOptions.rateNeverButton.let { button ->
            Assertions.assertThat(button?.textId).isEqualTo(INT_RES_ID)
            Assertions.assertThat(button?.rateDialogClickListener).isEqualTo(clickListener)
        }
    }

    @Test
    fun `title text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setTitleTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.titleTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `message text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setMessageTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.messageTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `confirm button text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setConfirmButtonTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.confirmButtonTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `store rating title text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setStoreRatingTitleTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.storeRatingTitleTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `store rating message text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setStoreRatingMessageTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.storeRatingMessageTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `rate now button text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setRateNowButtonTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.rateNowButton.textId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `rate now button clicklistener is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setRateNowButtonClickListener(clickListener)
        Assertions.assertThat(DialogOptions.rateNowButton.rateDialogClickListener).isEqualTo(clickListener)
    }

    @Test
    fun `feedback title text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setFeedbackTitleTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.feedbackTitleTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `no feedback button is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setNoFeedbackButton(INT_RES_ID, clickListener)
        DialogOptions.noFeedbackButton.let { button ->
            Assertions.assertThat(button.textId).isEqualTo(INT_RES_ID)
            Assertions.assertThat(button.rateDialogClickListener).isEqualTo(clickListener)
        }
    }

    @Test
    fun `mail feedback message text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setMailFeedbackMessageTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.mailFeedbackMessageTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `mail settings are set correctly into DialogOptions`() {
        val mailSettings = MailSettings("address", "subject", "body", "chooserTitle")
        AppRating.Builder(activity).setMailSettingsForFeedbackDialog(mailSettings)
        Assertions.assertThat(DialogOptions.mailSettings).isEqualTo(mailSettings)
    }

    @Test
    fun `mail feedback button is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setMailFeedbackButton(INT_RES_ID, clickListener)
        DialogOptions.mailFeedbackButton.let { button ->
            Assertions.assertThat(button.textId).isEqualTo(INT_RES_ID)
            Assertions.assertThat(button.rateDialogClickListener).isEqualTo(clickListener)
        }
    }

    @Test
    fun `custom feedback enabled set correctly into DialogOptions`() {
        AppRating.Builder(activity).setUseCustomFeedback(true)
        Assertions.assertThat(DialogOptions.useCustomFeedback).isEqualTo(true)
    }

    @Test
    fun `custom feedback message text is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setCustomFeedbackMessageTextId(INT_RES_ID)
        Assertions.assertThat(DialogOptions.customFeedbackMessageTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `custom feedback button is set correctly into DialogOptions`() {
        val clickListener = object : CustomFeedbackButtonClickListener {
            override fun onClick(userFeedbackText: String) {
            }
        }
        AppRating.Builder(activity).setCustomFeedbackButton(INT_RES_ID, clickListener)
        DialogOptions.customFeedbackButton.let { button ->
            Assertions.assertThat(button.textId).isEqualTo(INT_RES_ID)
            Assertions.assertThat(button.customFeedbackButtonClickListener).isEqualTo(clickListener)
        }
    }

    @Test
    fun `rating threshold is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setRatingThreshold(RatingThreshold.FOUR_AND_A_HALF)
        Assertions.assertThat(DialogOptions.ratingThreshold).isEqualTo(RatingThreshold.FOUR_AND_A_HALF)
    }

    @Test
    fun `cancelable is set correctly into DialogOptions`() {
        AppRating.Builder(activity).setCancelable(true)
        Assertions.assertThat(DialogOptions.cancelable).isEqualTo(true)
    }

    @Nested
    inner class PreferenceUtilChanges {
        @MockK
        lateinit var sharedPreferences: SharedPreferences

        @BeforeEach
        fun setup() {
            mockkObject(PreferenceUtil)
            every { PreferenceUtil.getPreferences(activity) } returns sharedPreferences
            every { sharedPreferences.edit() } returns mockk(relaxed = true)
        }

        @Test
        fun `reset triggers reset of PreferenceUtil`() {
            AppRating.reset(activity)
            verify { PreferenceUtil.reset(activity) }
        }

        @Test
        fun `minimum launch times is set correctly into PreferenceUtil`() {
            AppRating.Builder(activity).setMinimumLaunchTimes(10)
            verify { PreferenceUtil.setMinimumLaunchTimes(activity, 10) }
        }

        @Test
        fun `minimum launch times to show again is set correctly into PreferenceUtil`() {
            AppRating.Builder(activity).setMinimumDaysToShowAgain(10)
            verify { PreferenceUtil.setMinimumDaysToShowAgain(activity, 10) }
        }

        @Test
        fun `minimum days is set correctly into PreferenceUtil`() {
            AppRating.Builder(activity).setMinimumDays(10)
            verify { PreferenceUtil.setMinimumDays(activity, 10) }
        }

        @Test
        fun `minimum days to show again is set correctly into PreferenceUtil`() {
            AppRating.Builder(activity).setMinimumDaysToShowAgain(10)
            verify { PreferenceUtil.setMinimumDaysToShowAgain(activity, 10) }
        }
    }

    @Test
    fun `logging enabled is set correctly into RatingLogger`() {
        AppRating.Builder(activity).setLoggingEnabled(false)
        Assertions.assertThat(RatingLogger.isLoggingEnabled).isEqualTo(false)
    }

    @Test
    fun `debug is set correctly into RatingLogger`() {
        val builder = AppRating.Builder(activity)
        builder.setDebug(true)
        Assertions.assertThat(builder.isDebug).isEqualTo(true)
    }

    @Nested
    inner class DialogFragmentTestCalls {
        @Test
        fun `show now calls show function of RateDialogFragment`() {
            mockkConstructor(RateDialogFragment::class)
            mockkConstructor(Bundle::class)
            every { anyConstructed<Bundle>().putSerializable(any(), any()) } just Runs
            every { anyConstructed<RateDialogFragment>().show(any<FragmentManager>(), any()) } just Runs
            every { activity.supportFragmentManager } returns mockk()

            AppRating.Builder(activity).showNow()
            verify(exactly = 1) { anyConstructed<RateDialogFragment>().show(any<FragmentManager>(), any()) }
        }

        @Test
        fun `show if meets conditions increases launch times`() {
            mockkObject(PreferenceUtil)
            every { PreferenceUtil.increaseLaunchTimes(any()) } just Runs

            mockkObject(ConditionsChecker)
            every { ConditionsChecker.shouldShowDialog(activity) } returns false

            AppRating.Builder(activity).showIfMeetsConditions()
            verify(exactly = 1) { PreferenceUtil.increaseLaunchTimes(activity) }
        }

        @Test
        fun `show if meets conditions calls show now if conditions are met`() {
            mockkObject(PreferenceUtil)
            every { PreferenceUtil.increaseLaunchTimes(any()) } just Runs

            mockkObject(ConditionsChecker)
            every { ConditionsChecker.shouldShowDialog(activity) } returns true

            mockkConstructor(AppRating.Builder::class)
            every { anyConstructed<AppRating.Builder>().showNow() } just Runs

            AppRating.Builder(activity).showIfMeetsConditions()
            verify(exactly = 1) { anyConstructed<AppRating.Builder>().showNow() }
        }

        @Test
        fun `show if meets conditions doesn't call show now if conditions are met`() {
            mockkObject(PreferenceUtil)
            every { PreferenceUtil.increaseLaunchTimes(any()) } just Runs

            mockkObject(ConditionsChecker)
            every { ConditionsChecker.shouldShowDialog(activity) } returns false

            mockkConstructor(AppRating.Builder::class)
            every { anyConstructed<AppRating.Builder>().showNow() } just Runs

            AppRating.Builder(activity).showIfMeetsConditions()
            verify(exactly = 0) { anyConstructed<AppRating.Builder>().showNow() }
        }
    }

    @Test
    fun `initialize rate now button sets text and click listener correctly into DialogOptions`() {
        AppRating.Builder(activity).initializeRateNowButton()

        assertThat(DialogOptions.rateNowButton.textId).isEqualTo(R.string.rating_dialog_store_button_rate_now)
        assertThat(DialogOptions.rateNowButton.rateDialogClickListener).isNotNull
    }

    companion object {
        private const val INT_RES_ID = 42
        private val clickListener = object : RateDialogClickListener {
            override fun onClick() {
            }
        }
    }
}
