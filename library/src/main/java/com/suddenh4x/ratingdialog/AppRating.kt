package com.suddenh4x.ratingdialog

import android.content.Context
import androidx.activity.ComponentActivity
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.suddenh4x.ratingdialog.dialog.DialogOptions
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.ConditionsChecker
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.utils.FeedbackUtils

object AppRating {

    fun reset(context: Context) {
        PreferenceUtil.reset(context)
        RatingLogger.warn(context.getString(R.string.rating_dialog_log_settings_reset))
    }

    fun wasLaterButtonClicked(context: Context) = PreferenceUtil.wasLaterButtonClicked(context)

    fun openMailFeedback(context: Context, mailSettings: MailSettings) = FeedbackUtils.openMailFeedback(context, mailSettings)

    fun openPlayStoreListing(context: Context) = FeedbackUtils.openPlayStoreListing(context)

    data class Builder(var componentActivity: ComponentActivity) {
        internal var isDebug = false
        private val reviewManager: ReviewManager by lazy { ReviewManagerFactory.create(componentActivity) }
        private var dialogOptions = DialogOptions()

        internal constructor(componentActivity: ComponentActivity, dialogOptions: DialogOptions) : this(componentActivity) {
            this.dialogOptions = dialogOptions
        }

        /**
         * rating dialog mail feedback
         */

        fun setMailSettingsForFeedbackDialog(mailSettings: MailSettings) = apply {
            dialogOptions.mailSettings = mailSettings
        }

        /**
         * other settings
         */

        fun setMinimumLaunchTimes(launchTimes: Int) = apply {
            PreferenceUtil.setMinimumLaunchTimes(componentActivity, launchTimes)
        }

        fun setMinimumLaunchTimesToShowAgain(launchTimesToShowAgain: Int) = apply {
            PreferenceUtil.setMinimumLaunchTimesToShowAgain(componentActivity, launchTimesToShowAgain)
        }

        fun setMinimumDays(minimumDays: Int) = apply {
            PreferenceUtil.setMinimumDays(componentActivity, minimumDays)
        }

        fun setMinimumDaysToShowAgain(minimumDaysToShowAgain: Int) = apply {
            PreferenceUtil.setMinimumDaysToShowAgain(componentActivity, minimumDaysToShowAgain)
        }

        fun setCustomCondition(customCondition: () -> Boolean) = apply {
            dialogOptions.customCondition = customCondition
            RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_set_custom_condition))
        }

        fun setCustomConditionToShowAgain(customConditionToShowAgain: () -> Boolean) = apply {
            dialogOptions.customConditionToShowAgain = customConditionToShowAgain
            RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_set_custom_condition_to_show_again))
        }

        fun dontCountThisAsAppLaunch() = apply {
            dialogOptions.countAppLaunch = false
            RatingLogger.debug(componentActivity.getString(R.string.rating_dialog_log_dont_count_app_launch))
        }

        fun setLoggingEnabled(isLoggingEnabled: Boolean) = apply {
            RatingLogger.isLoggingEnabled = isLoggingEnabled
        }

        fun setDebug(isDebug: Boolean) = apply {
            this.isDebug = isDebug
            RatingLogger.warn(componentActivity.getString(R.string.rating_dialog_log_set_debug, isDebug))
        }

        /**
         * The completion listener will be invoked with true if the in-app review flow started
         * correctly (otherwise false).
         * Note: true doesn't mean that the in-app review from Google was displayed.
         */
        fun setGoogleInAppReviewCompleteListener(googleInAppReviewCompleteListener: (Boolean) -> Unit) = apply {
            dialogOptions.googleInAppReviewCompleteListener = googleInAppReviewCompleteListener
        }

        fun showNow() {
            RatingLogger.info(componentActivity.getString(R.string.rating_dialog_log_show_in_app_review))
            showGoogleInAppReview()
        }

        fun showIfMeetsConditions(): Boolean {
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
