package com.suddenh4x.ratingdialog

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.suddenh4x.ratingdialog.buttons.ConfirmButtonClickListener
import com.suddenh4x.ratingdialog.buttons.CustomFeedbackButtonClickListener
import com.suddenh4x.ratingdialog.buttons.RateButton
import com.suddenh4x.ratingdialog.buttons.RateDialogClickListener
import com.suddenh4x.ratingdialog.dialog.DialogOptions
import com.suddenh4x.ratingdialog.dialog.RateDialogFragment
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.ConditionsChecker
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.preferences.RatingThreshold
import com.suddenh4x.ratingdialog.utils.FeedbackUtils

object AppRating {

    fun reset(context: Context) {
        PreferenceUtil.reset(context)
        RatingLogger.warn("Settings were reset.")
    }

    fun isDialogAgreed(context: Context) = PreferenceUtil.isDialogAgreed(context)

    fun wasLaterButtonClicked(context: Context) = PreferenceUtil.wasLaterButtonClicked(context)

    fun wasNeverButtonClicked(context: Context) = PreferenceUtil.isDoNotShowAgain(context)

    fun getNumberOfLaterButtonClicks(context: Context) =
        PreferenceUtil.getNumberOfLaterButtonClicks(context)

    fun openMailFeedback(context: Context, mailSettings: MailSettings) =
        FeedbackUtils.openMailFeedback(context, mailSettings)

    fun openPlayStoreListing(context: Context) = FeedbackUtils.openPlayStoreListing(context)

    data class Builder(var activity: AppCompatActivity) {
        internal var isDebug = false
        internal var reviewManger: ReviewManager? = null
        private var dialogOptions = DialogOptions()

        internal constructor(activity: AppCompatActivity, dialogOptions: DialogOptions) :
            this(activity) {
            this.dialogOptions = dialogOptions
        }

        fun setIconDrawable(iconDrawable: Drawable?) = apply {
            dialogOptions.iconDrawable = iconDrawable
            RatingLogger.debug("Use custom icon drawable.")
        }

        fun setRateLaterButtonTextId(@StringRes rateLaterButtonTextId: Int) = apply {
            dialogOptions.rateLaterButton.textId = rateLaterButtonTextId
        }

        fun setRateLaterButtonClickListener(rateLaterButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.rateLaterButton.rateDialogClickListener =
                    rateLaterButtonClickListener
            }

        fun showRateNeverButton(
            @StringRes rateNeverButtonTextId: Int = R.string.rating_dialog_button_rate_never,
            rateNeverButtonClickListener: RateDialogClickListener? = null
        ) = apply {
            dialogOptions.rateNeverButton =
                RateButton(rateNeverButtonTextId, rateNeverButtonClickListener)
            RatingLogger.debug("Show rate never button.")
        }

        fun showRateNeverButtonAfterNTimes(
            @StringRes rateNeverButtonTextId: Int = R.string.rating_dialog_button_rate_never,
            rateNeverButtonClickListener: RateDialogClickListener? = null,
            countOfLaterButtonClicks: Int
        ) = apply {
            dialogOptions.rateNeverButton =
                RateButton(rateNeverButtonTextId, rateNeverButtonClickListener)
            dialogOptions.countOfLaterButtonClicksToShowNeverButton = countOfLaterButtonClicks
            RatingLogger.debug("Show rate never button after $countOfLaterButtonClicks later button clicks.")
        }

        // rating dialog overview
        fun setTitleTextId(@StringRes titleTextId: Int) = apply {
            dialogOptions.titleTextId = titleTextId
        }

        fun setMessageTextId(@StringRes messageTextId: Int) = apply {
            dialogOptions.messageTextId = messageTextId
        }

        fun setConfirmButtonTextId(@StringRes confirmButtonTextId: Int) = apply {
            dialogOptions.confirmButton.textId = confirmButtonTextId
        }

        fun setConfirmButtonClickListener(confirmButtonClickListener: ConfirmButtonClickListener) =
            apply {
                dialogOptions.confirmButton.confirmButtonClickListener = confirmButtonClickListener
            }

        fun setShowOnlyFullStars(showOnlyFullStars: Boolean) = apply {
            dialogOptions.showOnlyFullStars = showOnlyFullStars
        }

        // rating dialog store
        fun setStoreRatingTitleTextId(@StringRes storeRatingTitleTextId: Int) = apply {
            dialogOptions.storeRatingTitleTextId = storeRatingTitleTextId
        }

        fun setStoreRatingMessageTextId(@StringRes storeRatingMessageTextId: Int) = apply {
            dialogOptions.storeRatingMessageTextId = storeRatingMessageTextId
        }

        fun setRateNowButtonTextId(@StringRes rateNowButtonTextId: Int) = apply {
            dialogOptions.rateNowButton.textId = rateNowButtonTextId
        }

        fun overwriteRateNowButtonClickListener(rateNowButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.rateNowButton.rateDialogClickListener = rateNowButtonClickListener
            }

        fun setAdditionalRateNowButtonClickListener(additionalRateNowButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.additionalRateNowButtonClickListener =
                    additionalRateNowButtonClickListener
            }

        // rating dialog feedback
        fun setFeedbackTitleTextId(@StringRes feedbackTitleTextId: Int) = apply {
            dialogOptions.feedbackTitleTextId = feedbackTitleTextId
        }

        fun setNoFeedbackButtonTextId(@StringRes noFeedbackButtonTextId: Int) = apply {
            dialogOptions.noFeedbackButton.textId = noFeedbackButtonTextId
        }

        fun setNoFeedbackButtonClickListener(noFeedbackButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.noFeedbackButton.rateDialogClickListener =
                    noFeedbackButtonClickListener
            }

        // rating dialog mail feedback
        fun setMailFeedbackMessageTextId(@StringRes feedbackMailMessageTextId: Int) = apply {
            dialogOptions.mailFeedbackMessageTextId = feedbackMailMessageTextId
        }

        fun setMailSettingsForFeedbackDialog(mailSettings: MailSettings) = apply {
            dialogOptions.mailSettings = mailSettings
        }

        fun setMailFeedbackButtonTextId(@StringRes mailFeedbackButtonTextId: Int) = apply {
            dialogOptions.mailFeedbackButton.textId = mailFeedbackButtonTextId
        }

        fun overwriteMailFeedbackButtonClickListener(mailFeedbackButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.mailFeedbackButton.rateDialogClickListener =
                    mailFeedbackButtonClickListener
            }

        fun setAdditionalMailFeedbackButtonClickListener(
            additionalMailFeedbackButtonClickListener: RateDialogClickListener
        ) =
            apply {
                dialogOptions.additionalMailFeedbackButtonClickListener =
                    additionalMailFeedbackButtonClickListener
            }

        // rating dialog custom feedback
        fun setUseCustomFeedback(useCustomFeedback: Boolean) = apply {
            dialogOptions.useCustomFeedback = useCustomFeedback
            RatingLogger.debug("Use custom feedback instead of mail feedback: $useCustomFeedback.")
        }

        fun setCustomFeedbackMessageTextId(@StringRes feedbackCustomMessageTextId: Int) = apply {
            dialogOptions.customFeedbackMessageTextId = feedbackCustomMessageTextId
        }

        fun setCustomFeedbackButtonTextId(@StringRes customFeedbackButtonTextId: Int) = apply {
            dialogOptions.customFeedbackButton.textId = customFeedbackButtonTextId
        }

        fun setCustomFeedbackButtonClickListener(customFeedbackButtonClickListener: CustomFeedbackButtonClickListener) =
            apply {
                dialogOptions.customFeedbackButton.customFeedbackButtonClickListener =
                    customFeedbackButtonClickListener
            }

        // other settings
        fun setRatingThreshold(ratingThreshold: RatingThreshold) = apply {
            dialogOptions.ratingThreshold = ratingThreshold
            RatingLogger.debug("Set rating threshold to ${ratingThreshold.ordinal / 2}.")
        }

        fun setCancelable(cancelable: Boolean) = apply {
            dialogOptions.cancelable = cancelable
            RatingLogger.debug("Set cancelable to $cancelable.")
        }

        fun setMinimumLaunchTimes(launchTimes: Int) = apply {
            PreferenceUtil.setMinimumLaunchTimes(activity, launchTimes)
        }

        fun setMinimumLaunchTimesToShowAgain(launchTimesToShowAgain: Int) = apply {
            PreferenceUtil.setMinimumLaunchTimesToShowAgain(
                activity,
                launchTimesToShowAgain
            )
        }

        fun setMinimumDays(minimumDays: Int) = apply {
            PreferenceUtil.setMinimumDays(activity, minimumDays)
        }

        fun setMinimumDaysToShowAgain(minimumDaysToShowAgain: Int) = apply {
            PreferenceUtil.setMinimumDaysToShowAgain(activity, minimumDaysToShowAgain)
        }

        fun setCustomCondition(customCondition: () -> Boolean) = apply {
            dialogOptions.customCondition = customCondition
            RatingLogger.debug(
                "Custom condition set. This condition will be removed next" +
                    " time you call the Builder constructor."
            )
        }

        fun setCustomConditionToShowAgain(customConditionToShowAgain: () -> Boolean) = apply {
            dialogOptions.customConditionToShowAgain = customConditionToShowAgain
            RatingLogger.debug(
                "Custom condition to show again set. This condition will" +
                    "be removed next time you call the Builder constructor."
            )
        }

        fun dontCountThisAsAppLaunch() = apply {
            dialogOptions.countAppLaunch = false
            RatingLogger.debug(
                "countAppLaunch is now set to false. This setting will be " +
                    "reset next time you call the Builder constructor."
            )
        }

        fun setLoggingEnabled(isLoggingEnabled: Boolean) = apply {
            RatingLogger.isLoggingEnabled = isLoggingEnabled
        }

        fun setDebug(isDebug: Boolean) = apply {
            this.isDebug = isDebug
            RatingLogger.warn("Set debug to $isDebug. Don't use this for production.")
        }

        // Google in-app review
        /**
         * If this method is called, the in-app review from Google will be used instead of
         * the library dialog.
         */
        fun useGoogleInAppReview() = apply {
            reviewManger = ReviewManagerFactory.create(activity)
            dialogOptions.useGoogleInAppReview = true
            RatingLogger.info("Use in-app review from Google instead of the library dialog.")
        }

        /**
         * The completion listener will be invoked with true if the in-app review flow started
         * correctly (otherwise false).
         * Note: true doesn't mean that the in-app review from Google was displayed.
         */
        fun setGoogleInAppReviewCompleteListener(googleInAppReviewCompleteListener: (Boolean) -> Unit) =
            apply {
                dialogOptions.googleInAppReviewCompleteListener = googleInAppReviewCompleteListener
            }

        /**
         * This method will return null if the in-app review from Google is used.
         */
        fun create(): DialogFragment? {
            return if (dialogOptions.useGoogleInAppReview) {
                RatingLogger.warn("In-app review from Google will be used. Can't create the library dialog.")
                null
            } else {
                RateDialogFragment.newInstance(dialogOptions)
            }
        }

        fun showNow() {
            if (dialogOptions.useGoogleInAppReview) {
                RatingLogger.info("In-app review from Google will be displayed now.")
                showGoogleInAppReview()
            } else {
                RatingLogger.debug("In-app review from Google hasn't been activated. Showing library dialog now.")
                RateDialogFragment.newInstance(dialogOptions)
                    .show(activity.supportFragmentManager, TAG)
            }
        }

        fun showIfMeetsConditions() {
            if (dialogOptions.countAppLaunch) {
                RatingLogger.debug("App launch will be counted: countAppLaunch is true.")
                PreferenceUtil.increaseLaunchTimes(activity)
            } else {
                RatingLogger.info("App launch not counted this time: countAppLaunch has been set to false.")
            }

            if (isDebug || ConditionsChecker.shouldShowDialog(activity, dialogOptions)) {
                RatingLogger.info("Show rating dialog now: Conditions met.")
                showNow()
            } else {
                RatingLogger.info("Don't show rating dialog: Conditions not met.")
            }
        }

        internal fun showGoogleInAppReview() {
            val requestTask = reviewManger?.requestReviewFlow()
            requestTask?.addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    val reviewInfo = request.result
                    val flow = reviewManger?.launchReviewFlow(activity, reviewInfo)
                    flow?.addOnCompleteListener { task ->
                        RatingLogger.info("Google in-app review request completed.")
                        PreferenceUtil.onGoogleInAppReviewFlowCompleted(activity)
                        dialogOptions.googleInAppReviewCompleteListener?.invoke(task.isSuccessful)
                            ?: RatingLogger.warn("There's no completeListener for Google's in-app review.")
                    }
                } else {
                    RatingLogger.info("Google in-app review request wasn't successful.")
                    dialogOptions.googleInAppReviewCompleteListener?.invoke(false)
                        ?: RatingLogger.warn("There's no completeListener for Google's in-app review.")
                }
            }
        }

        companion object {
            private val TAG = AppRating::class.java.simpleName
        }
    }
}
