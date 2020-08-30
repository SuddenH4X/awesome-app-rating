package com.suddenh4x.ratingdialog

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.suddenh4x.ratingdialog.buttons.ConfirmButtonClickListener
import com.suddenh4x.ratingdialog.buttons.CustomFeedbackButtonClickListener
import com.suddenh4x.ratingdialog.buttons.RateDialogClickListener
import com.suddenh4x.ratingdialog.dialog.DialogOptions
import com.suddenh4x.ratingdialog.dialog.RateDialogFragment
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.ConditionsChecker
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.preferences.RatingThreshold
import com.suddenh4x.ratingdialog.utils.FeedbackUtils
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
    }

    @Test
    fun `icon drawable is set correctly into dialogOptions`() {
        val drawable = mockk<Drawable>()
        getBuilder().setIconDrawable(drawable)
        assertThat(dialogOptions.iconDrawable).isEqualTo(drawable)
    }

    @Test
    fun `rate later button text is set correctly into dialogOptions`() {
        getBuilder().setRateLaterButtonTextId(INT_RES_ID)
        assertThat(dialogOptions.rateLaterButton.textId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `rate later button click listener is set correctly into dialogOptions`() {
        getBuilder().setRateLaterButtonClickListener(clickListener)
        assertThat(dialogOptions.rateLaterButton.rateDialogClickListener).isEqualTo(clickListener)
    }

    @Test
    fun `rate never button is set correctly into dialogOptions`() {
        getBuilder().showRateNeverButton(INT_RES_ID, clickListener)
        dialogOptions.rateNeverButton.let { button ->
            assertThat(button?.textId).isEqualTo(INT_RES_ID)
            assertThat(button?.rateDialogClickListener).isEqualTo(clickListener)
        }
    }

    @Test
    fun `count of later button clicks to show never button is set correctly into dialogOptions`() {
        getBuilder().showRateNeverButtonAfterNTimes(INT_RES_ID, clickListener, 42)
        assertThat(dialogOptions.countOfLaterButtonClicksToShowNeverButton).isEqualTo(42)
    }

    @Test
    fun `title text is set correctly into dialogOptions`() {
        getBuilder().setTitleTextId(INT_RES_ID)
        assertThat(dialogOptions.titleTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `message text is set correctly into dialogOptions`() {
        getBuilder().setMessageTextId(INT_RES_ID)
        assertThat(dialogOptions.messageTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `confirm button text is set correctly into dialogOptions`() {
        getBuilder().setConfirmButtonTextId(INT_RES_ID)
        assertThat(dialogOptions.confirmButton.textId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `confirm button click listener is set correctly into dialogOptions`() {
        getBuilder().setConfirmButtonClickListener(confirmButtonClickListener)
        assertThat(dialogOptions.confirmButton.confirmButtonClickListener).isEqualTo(
            confirmButtonClickListener
        )
    }

    @Test
    fun `full star rating is set correctly into dialogOptions`() {
        getBuilder().setShowOnlyFullStars(true)
        assertThat(dialogOptions.showOnlyFullStars).isEqualTo(true)
    }

    @Test
    fun `store rating title text is set correctly into dialogOptions`() {
        getBuilder().setStoreRatingTitleTextId(INT_RES_ID)
        assertThat(dialogOptions.storeRatingTitleTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `store rating message text is set correctly into dialogOptions`() {
        getBuilder().setStoreRatingMessageTextId(INT_RES_ID)
        assertThat(dialogOptions.storeRatingMessageTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `rate now button text is set correctly into dialogOptions`() {
        getBuilder().setRateNowButtonTextId(INT_RES_ID)
        assertThat(dialogOptions.rateNowButton.textId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `rate now button click listener is set correctly into dialogOptions`() {
        getBuilder().overwriteRateNowButtonClickListener(clickListener)
        assertThat(dialogOptions.rateNowButton.rateDialogClickListener)
            .isEqualTo(clickListener)
    }

    @Test
    fun `additional rate now button click listener is set correctly into dialogOptions`() {
        getBuilder().setAdditionalRateNowButtonClickListener(clickListener)
        assertThat(dialogOptions.additionalRateNowButtonClickListener)
            .isEqualTo(clickListener)
    }

    @Test
    fun `feedback title text is set correctly into dialogOptions`() {
        getBuilder().setFeedbackTitleTextId(INT_RES_ID)
        assertThat(dialogOptions.feedbackTitleTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `no feedback button text is set correctly into dialogOptions`() {
        getBuilder().setNoFeedbackButtonTextId(INT_RES_ID)
        assertThat(dialogOptions.noFeedbackButton.textId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `no feedback button click listener is set correctly into dialogOptions`() {
        getBuilder().setNoFeedbackButtonClickListener(clickListener)
        assertThat(dialogOptions.noFeedbackButton.rateDialogClickListener).isEqualTo(clickListener)
    }

    @Test
    fun `mail feedback message text is set correctly into dialogOptions`() {
        getBuilder().setMailFeedbackMessageTextId(INT_RES_ID)
        assertThat(dialogOptions.mailFeedbackMessageTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `mail settings are set correctly into dialogOptions`() {
        getBuilder().setMailSettingsForFeedbackDialog(mailSettings)
        assertThat(dialogOptions.mailSettings).isEqualTo(mailSettings)
    }

    @Test
    fun `mail feedback button text is set correctly into dialogOptions`() {
        getBuilder().setMailFeedbackButtonTextId(INT_RES_ID)
        assertThat(dialogOptions.mailFeedbackButton.textId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `mail feedback button click listener is set correctly into dialogOptions`() {
        getBuilder().overwriteMailFeedbackButtonClickListener(clickListener)
        assertThat(dialogOptions.mailFeedbackButton.rateDialogClickListener).isEqualTo(clickListener)
    }

    @Test
    fun `additional mail feedback button click listener is set correctly into dialogOptions`() {
        getBuilder().setAdditionalMailFeedbackButtonClickListener(clickListener)
        assertThat(dialogOptions.additionalMailFeedbackButtonClickListener)
            .isEqualTo(clickListener)
    }

    @Test
    fun `custom feedback enabled set correctly into dialogOptions`() {
        getBuilder().setUseCustomFeedback(true)
        assertThat(dialogOptions.useCustomFeedback).isEqualTo(true)
    }

    @Test
    fun `custom feedback message text is set correctly into dialogOptions`() {
        getBuilder().setCustomFeedbackMessageTextId(INT_RES_ID)
        assertThat(dialogOptions.customFeedbackMessageTextId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `custom feedback button text is set correctly into dialogOptions`() {
        getBuilder().setCustomFeedbackButtonTextId(INT_RES_ID)
        assertThat(dialogOptions.customFeedbackButton.textId).isEqualTo(INT_RES_ID)
    }

    @Test
    fun `custom feedback button click listener is set correctly into dialogOptions`() {
        getBuilder()
            .setCustomFeedbackButtonClickListener(customFeedbackButtonClickListener)
        assertThat(dialogOptions.customFeedbackButton.customFeedbackButtonClickListener)
            .isEqualTo(customFeedbackButtonClickListener)
    }

    @Test
    fun `rating threshold is set correctly into dialogOptions`() {
        getBuilder().setRatingThreshold(RatingThreshold.FOUR_AND_A_HALF)
        assertThat(dialogOptions.ratingThreshold).isEqualTo(RatingThreshold.FOUR_AND_A_HALF)
    }

    @Test
    fun `cancelable is set correctly into dialogOptions`() {
        getBuilder().setCancelable(true)
        assertThat(dialogOptions.cancelable).isEqualTo(true)
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
    inner class UseGoogleInAppReview {
        @MockK
        lateinit var reviewManger: ReviewManager

        @BeforeEach
        fun setup() {
            mockkStatic(ReviewManagerFactory::class)
            every { ReviewManagerFactory.create(any()) } returns reviewManger
        }

        @Test
        fun `sets useGoogleInAppReview to true within dialogOptions`() {
            assertFalse(dialogOptions.useGoogleInAppReview)

            getBuilder().useGoogleInAppReview()

            assertTrue(dialogOptions.useGoogleInAppReview)
        }

        @Test
        fun `creates review manager`() {
            val builder = getBuilder()
            assertThat(builder.reviewManger).isNull()

            builder.useGoogleInAppReview()

            assertThat(builder.reviewManger).isEqualTo(reviewManger)
        }
    }

    @Test
    fun `google in-app review complete listener is set correctly into dialogOptions`() {
        val completeListener: (Boolean) -> Unit = mockk()
        getBuilder().setGoogleInAppReviewCompleteListener(completeListener)
        assertThat(dialogOptions.googleInAppReviewCompleteListener).isEqualTo(completeListener)
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
        fun `is dialog agreed calls correct method of PreferenceUtil`() {
            AppRating.isDialogAgreed(activity)
            verify { PreferenceUtil.isDialogAgreed(activity) }
        }

        @Test
        fun `was later button clicked calls correct method of PreferenceUtil`() {
            AppRating.wasLaterButtonClicked(activity)
            verify { PreferenceUtil.wasLaterButtonClicked(activity) }
        }

        @Test
        fun `was never button clicked calls correct method of PreferenceUtil`() {
            AppRating.wasNeverButtonClicked(activity)
            verify { PreferenceUtil.isDoNotShowAgain(activity) }
        }

        @Test
        fun `get number of later button clicks calls correct method of PreferenceUtil`() {
            AppRating.getNumberOfLaterButtonClicks(activity)
            verify { PreferenceUtil.getNumberOfLaterButtonClicks(activity) }
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

    @Nested
    inner class DialogFragmentTestCalls {
        @Test
        fun `show now calls show function of RateDialogFragment`() {
            mockkConstructor(RateDialogFragment::class)
            mockkConstructor(Bundle::class)
            every { anyConstructed<Bundle>().putSerializable(any(), any()) } just Runs
            every {
                anyConstructed<RateDialogFragment>().show(
                    any<FragmentManager>(),
                    any()
                )
            } just Runs
            every { activity.supportFragmentManager } returns mockk()

            getBuilder().showNow()
            verify(exactly = 1) { anyConstructed<Bundle>().putSerializable(any(), any()) }
            verify(exactly = 1) {
                anyConstructed<RateDialogFragment>().show(
                    any<FragmentManager>(),
                    any()
                )
            }
        }

        @Test
        fun `show if meets conditions increases launch times`() {
            mockkObject(PreferenceUtil)
            every { PreferenceUtil.increaseLaunchTimes(any()) } just Runs

            mockkObject(ConditionsChecker)
            every { ConditionsChecker.shouldShowDialog(activity, dialogOptions) } returns false

            getBuilder().showIfMeetsConditions()
            verify(exactly = 1) { PreferenceUtil.increaseLaunchTimes(activity) }
        }

        @Test
        fun `show if meets conditions calls show now if conditions are met`() {
            mockkObject(PreferenceUtil)
            every { PreferenceUtil.increaseLaunchTimes(any()) } just Runs

            mockkObject(ConditionsChecker)
            every { ConditionsChecker.shouldShowDialog(activity, dialogOptions) } returns true

            mockkConstructor(AppRating.Builder::class)
            every { anyConstructed<AppRating.Builder>().showNow() } just Runs

            getBuilder().showIfMeetsConditions()
            verify(exactly = 1) { anyConstructed<AppRating.Builder>().showNow() }
        }

        @Test
        fun `show if meets conditions doesn't call show now if conditions are met`() {
            mockkObject(PreferenceUtil)
            every { PreferenceUtil.increaseLaunchTimes(any()) } just Runs

            mockkObject(ConditionsChecker)
            every { ConditionsChecker.shouldShowDialog(activity, dialogOptions) } returns false

            mockkConstructor(AppRating.Builder::class)
            every { anyConstructed<AppRating.Builder>().showNow() } just Runs

            getBuilder().showIfMeetsConditions()
            verify(exactly = 0) { anyConstructed<AppRating.Builder>().showNow() }
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
        private const val INT_RES_ID = 42
        private val clickListener = object : RateDialogClickListener {
            override fun onClick() {
            }
        }
        private val confirmButtonClickListener = object : ConfirmButtonClickListener {
            override fun onClick(userRating: Float) {
            }
        }
        private val customFeedbackButtonClickListener = object : CustomFeedbackButtonClickListener {
            override fun onClick(userFeedbackText: String) {
            }
        }
        private val customCondition: () -> Boolean = { true }
        private val mailSettings = MailSettings("mailAddress", "subject", "message", "errorToast")
    }
}
