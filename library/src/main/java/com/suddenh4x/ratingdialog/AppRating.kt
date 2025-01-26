package com.suddenh4x.ratingdialog

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.core.app.ComponentActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
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
import com.suddenh4x.ratingdialog.preferences.toFloat
import com.suddenh4x.ratingdialog.utils.FeedbackUtils

object AppRating {

    fun reset(context: Context) {
        PreferenceUtil.reset(context)
        RatingLogger.warn(context.getString(R.string.rating_dialog_log_settings_reset))
    }

    fun isDialogAgreed(context: Context) = PreferenceUtil.isDialogAgreed(context)

    fun wasLaterButtonClicked(context: Context) = PreferenceUtil.wasLaterButtonClicked(context)

    fun wasNeverButtonClicked(context: Context) = PreferenceUtil.isDoNotShowAgain(context)

    fun getNumberOfLaterButtonClicks(context: Context) = PreferenceUtil.getNumberOfLaterButtonClicks(context)

    fun openMailFeedback(
        context: Context,
        mailSettings: MailSettings,
    ) = FeedbackUtils.openMailFeedback(context, mailSettings)

    fun openPlayStoreListing(context: Context) = FeedbackUtils.openPlayStoreListing(context)

    data class Builder(var componentActivity: ComponentActivity) {

        internal var isDebug = false
        internal var reviewManager: ReviewManager? = null
        private var dialogOptions = DialogOptions()

        internal constructor(componentActivity: ComponentActivity, dialogOptions: DialogOptions) : this(componentActivity) {
            this.dialogOptions = dialogOptions
        }

        fun setIconDrawable(iconDrawable: Drawable?) =
            apply {
                dialogOptions.iconDrawable = iconDrawable
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_use_custom_icon))
            }

        fun setCustomTheme(customTheme: Int) =
            apply {
                dialogOptions.customTheme = customTheme
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_use_custom_theme))
            }

        fun setRateLaterButtonTextId(
            @StringRes rateLaterButtonTextId: Int,
        ) = apply {
            dialogOptions.rateLaterButton.textId = rateLaterButtonTextId
        }

        fun setRateLaterButtonClickListener(rateLaterButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.rateLaterButton.rateDialogClickListener = rateLaterButtonClickListener
            }

        fun showRateNeverButton(
            @StringRes rateNeverButtonTextId: Int = R.string.rating_dialog_button_rate_never,
            rateNeverButtonClickListener: RateDialogClickListener? = null,
        ) = apply {
            dialogOptions.rateNeverButton = RateButton(rateNeverButtonTextId, rateNeverButtonClickListener)
            RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_show_rate_never_button))
        }

        fun showRateNeverButtonAfterNTimes(
            @StringRes rateNeverButtonTextId: Int = R.string.rating_dialog_button_rate_never,
            rateNeverButtonClickListener: RateDialogClickListener? = null,
            countOfLaterButtonClicks: Int,
        ) = apply {
            dialogOptions.rateNeverButton = RateButton(rateNeverButtonTextId, rateNeverButtonClickListener)
            dialogOptions.countOfLaterButtonClicksToShowNeverButton = countOfLaterButtonClicks
            RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_show_rate_never_button_later, countOfLaterButtonClicks))
        }

        /**
         * rating dialog overview
         */

        fun setTitleTextId(
            @StringRes titleTextId: Int,
        ) = apply {
            dialogOptions.titleTextId = titleTextId
        }

        fun setMessageTextId(
            @StringRes messageTextId: Int,
        ) = apply {
            dialogOptions.messageTextId = messageTextId
        }

        fun setConfirmButtonTextId(
            @StringRes confirmButtonTextId: Int,
        ) = apply {
            dialogOptions.confirmButton.textId = confirmButtonTextId
        }

        fun setConfirmButtonClickListener(confirmButtonClickListener: ConfirmButtonClickListener) =
            apply {
                dialogOptions.confirmButton.confirmButtonClickListener = confirmButtonClickListener
            }

        fun setShowOnlyFullStars(showOnlyFullStars: Boolean) =
            apply {
                dialogOptions.showOnlyFullStars = showOnlyFullStars
            }

        /**
         * rating dialog store
         */

        fun setStoreRatingTitleTextId(
            @StringRes storeRatingTitleTextId: Int,
        ) = apply {
            dialogOptions.storeRatingTitleTextId = storeRatingTitleTextId
        }

        fun setStoreRatingMessageTextId(
            @StringRes storeRatingMessageTextId: Int,
        ) = apply {
            dialogOptions.storeRatingMessageTextId = storeRatingMessageTextId
        }

        fun setRateNowButtonTextId(
            @StringRes rateNowButtonTextId: Int,
        ) = apply {
            dialogOptions.rateNowButton.textId = rateNowButtonTextId
        }

        fun overwriteRateNowButtonClickListener(rateNowButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.rateNowButton.rateDialogClickListener = rateNowButtonClickListener
            }

        fun setAdditionalRateNowButtonClickListener(additionalRateNowButtonClickListener: RateDialogClickListener) =
            apply { dialogOptions.additionalRateNowButtonClickListener = additionalRateNowButtonClickListener }

        /**
         * rating dialog feedback
         */

        fun setFeedbackTitleTextId(
            @StringRes feedbackTitleTextId: Int,
        ) = apply {
            dialogOptions.feedbackTitleTextId = feedbackTitleTextId
        }

        fun setNoFeedbackButtonTextId(
            @StringRes noFeedbackButtonTextId: Int,
        ) = apply {
            dialogOptions.noFeedbackButton.textId = noFeedbackButtonTextId
        }

        fun setNoFeedbackButtonClickListener(noFeedbackButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.noFeedbackButton.rateDialogClickListener = noFeedbackButtonClickListener
            }

        /**
         * rating dialog mail feedback
         */

        fun setMailFeedbackMessageTextId(
            @StringRes feedbackMailMessageTextId: Int,
        ) = apply {
            dialogOptions.mailFeedbackMessageTextId = feedbackMailMessageTextId
        }

        fun setMailSettingsForFeedbackDialog(mailSettings: MailSettings) =
            apply {
                dialogOptions.mailSettings = mailSettings
            }

        fun setMailFeedbackButtonTextId(
            @StringRes mailFeedbackButtonTextId: Int,
        ) = apply {
            dialogOptions.mailFeedbackButton.textId = mailFeedbackButtonTextId
        }

        fun overwriteMailFeedbackButtonClickListener(mailFeedbackButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.mailFeedbackButton.rateDialogClickListener = mailFeedbackButtonClickListener
            }

        fun setAdditionalMailFeedbackButtonClickListener(additionalMailFeedbackButtonClickListener: RateDialogClickListener) =
            apply {
                dialogOptions.additionalMailFeedbackButtonClickListener = additionalMailFeedbackButtonClickListener
            }

        /**
         * rating dialog custom feedback
         */

        fun setUseCustomFeedback(useCustomFeedback: Boolean) =
            apply {
                dialogOptions.useCustomFeedback = useCustomFeedback
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_use_custom_feedback, useCustomFeedback))
            }

        fun setCustomFeedbackMessageTextId(
            @StringRes feedbackCustomMessageTextId: Int,
        ) = apply {
            dialogOptions.customFeedbackMessageTextId = feedbackCustomMessageTextId
        }

        fun setCustomFeedbackButtonTextId(
            @StringRes customFeedbackButtonTextId: Int,
        ) = apply {
            dialogOptions.customFeedbackButton.textId = customFeedbackButtonTextId
        }

        fun setCustomFeedbackButtonClickListener(customFeedbackButtonClickListener: CustomFeedbackButtonClickListener) =
            apply {
                dialogOptions.customFeedbackButton.customFeedbackButtonClickListener = customFeedbackButtonClickListener
            }

        /**
         * other settings
         */

        fun setRatingThreshold(ratingThreshold: RatingThreshold) =
            apply {
                dialogOptions.ratingThreshold = ratingThreshold
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_set_rating_threshold, ratingThreshold.toFloat()))
            }

        fun setCancelable(cancelable: Boolean) =
            apply {
                dialogOptions.cancelable = cancelable
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_set_cancelable, cancelable))
            }

        fun setDialogCancelListener(dialogCancelListener: () -> Unit) =
            apply {
                dialogOptions.dialogCancelListener = dialogCancelListener
            }

        fun setMinimumLaunchTimes(launchTimes: Int) =
            apply {
                PreferenceUtil.setMinimumLaunchTimes(componentActivity, launchTimes)
            }

        fun setMinimumLaunchTimesToShowAgain(launchTimesToShowAgain: Int) =
            apply {
                PreferenceUtil.setMinimumLaunchTimesToShowAgain(componentActivity, launchTimesToShowAgain)
            }

        fun setMinimumDays(minimumDays: Int) =
            apply {
                PreferenceUtil.setMinimumDays(componentActivity, minimumDays)
            }

        fun setMinimumDaysToShowAgain(minimumDaysToShowAgain: Int) =
            apply {
                PreferenceUtil.setMinimumDaysToShowAgain(componentActivity, minimumDaysToShowAgain)
            }

        fun setCustomCondition(customCondition: () -> Boolean) =
            apply {
                dialogOptions.customCondition = customCondition
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_set_custom_condition))
            }

        fun setCustomConditionToShowAgain(customConditionToShowAgain: () -> Boolean) =
            apply {
                dialogOptions.customConditionToShowAgain = customConditionToShowAgain
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_set_custom_condition_to_show_again))
            }

        fun dontCountThisAsAppLaunch() =
            apply {
                dialogOptions.countAppLaunch = false
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_dont_count_app_launch))
            }

        fun setLoggingEnabled(isLoggingEnabled: Boolean) =
            apply {
                RatingLogger.isLoggingEnabled = isLoggingEnabled
            }

        fun setDebug(isDebug: Boolean) =
            apply {
                this.isDebug = isDebug
                RatingLogger.warn(componentActivity.getString(R.string.rating_dialog_log_set_debug, isDebug))
            }

        /**
         * Google in-app review
         *
         *
         *
         * If this method is called, the in-app review from Google will be used instead of
         * the library dialog.
         */
        fun useGoogleInAppReview() =
            apply {
                reviewManager = ReviewManagerFactory.create(componentActivity)
                dialogOptions.useGoogleInAppReview = true
                RatingLogger.info(componentActivity.getString(R.string.rating_dialog_log_use_in_app_review))
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
        fun create(): DialogFragment? =
            when {
                dialogOptions.useGoogleInAppReview -> {
                    RatingLogger.warn(componentActivity.getString(R.string.rating_dialog_log_create_not_possible_with_in_app_review))
                    null
                }

                else -> RateDialogFragment.newInstance(dialogOptions)
            }

        fun showNow() =
            when {
                dialogOptions.useGoogleInAppReview -> {
                    RatingLogger.info(componentActivity.getString(R.string.rating_dialog_log_show_in_app_review))
                    showGoogleInAppReview()
                }

                else -> {
                    RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_show_library_dialog))
                    (componentActivity as? FragmentActivity)?.let {
                        RateDialogFragment.newInstance(dialogOptions).show(it.supportFragmentManager, TAG)
                    }
                        ?: RatingLogger.error(componentActivity.getString(R.string.rating_dialog_log_error_extend_from_fragment_activity))
                }
            }

        fun showIfMeetsConditions(): Boolean {
            (componentActivity as? FragmentActivity)?.let {
                if (it.supportFragmentManager.findFragmentByTag(TAG) != null) {
                    RatingLogger.info(componentActivity.getString(R.string.rating_dialog_log_stop_checking_conditions))
                    return false
                }
            }

            if (dialogOptions.countAppLaunch) {
                RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_app_launch_counted))
                PreferenceUtil.increaseLaunchTimes(componentActivity)
            } else {
                RatingLogger.info(componentActivity.getString(R.string.rating_dialog_log_app_launch_not_counted))
            }

            return if (isDebug || ConditionsChecker.shouldShowDialog(componentActivity, dialogOptions)) {
                RatingLogger.info(componentActivity.getString(R.string.rating_dialog_log_show_rating_dialog_now))
                showNow()
                true
            } else {
                RatingLogger.info(componentActivity.getString(R.string.rating_dialog_log_dont_show_rating_dialog_now))
                false
            }
        }

        internal fun showGoogleInAppReview() {
            val requestTask = reviewManager?.requestReviewFlow() ?: run {
                onGoogleInAppReviewFailure(componentActivity.getString(R.string.rating_dialog_log_in_app_review_review_manager_is_null))
                return
            }
            requestTask.addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    val reviewInfo = request.result ?: run {
                        onGoogleInAppReviewFailure(componentActivity.getString(R.string.rating_dialog_log_in_app_review_initial_request_is_null))
                        return@addOnCompleteListener
                    }
                    val flow = reviewManager?.launchReviewFlow(componentActivity, reviewInfo) ?: run {
                        onGoogleInAppReviewFailure(componentActivity.getString(R.string.rating_dialog_log_in_app_review_initial_request_is_null))
                        return@addOnCompleteListener
                    }
                    flow.addOnCompleteListener { task ->
                        RatingLogger.info(componentActivity.getString(R.string.rating_dialog_log_in_app_review_completed))
                        PreferenceUtil.onGoogleInAppReviewFlowCompleted(componentActivity)
                        dialogOptions.googleInAppReviewCompleteListener?.invoke(task.isSuccessful)
                            ?: RatingLogger.warn(componentActivity.getString(R.string.rating_dialog_log_in_app_review_no_complete_listener))
                    }
                } else {
                    onGoogleInAppReviewFailure(componentActivity.getString(R.string.rating_dialog_log_in_app_review_initial_request_not_successful))
                }
            }
        }

        private fun onGoogleInAppReviewFailure(additionalInfo: String) {
            RatingLogger.warn(componentActivity.getString(R.string.rating_dialog_log_in_app_review_not_successful, additionalInfo))
            dialogOptions.googleInAppReviewCompleteListener?.invoke(false)
                ?: RatingLogger.warn(componentActivity.getString(R.string.rating_dialog_log_in_app_review_no_complete_listener))
        }

        companion object {

            private const val TAG = "AwesomeAppRatingDialog"
        }
    }
}
