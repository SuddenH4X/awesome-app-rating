package com.suddenh4x.ratingdialog

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
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

object AppRating {

    fun reset(context: Context) {
        PreferenceUtil.reset(context)
        RatingLogger.warn("Settings were reset.")
    }

    data class Builder(var activity: AppCompatActivity) {
        internal var isDebug = false

        init {
            DialogOptions.apply {
                customCondition = null
                customConditionToShowAgain = null
                countAppLaunch = true
            }
        }

        fun setIconDrawable(iconDrawable: Drawable?) = apply {
            DialogOptions.iconDrawable = iconDrawable
            RatingLogger.debug("Use custom icon drawable.")
        }

        fun setRateLaterButtonTextId(@StringRes rateLaterButtonTextId: Int) = apply {
            DialogOptions.rateLaterButton.textId = rateLaterButtonTextId
        }

        fun setRateLaterButtonClickListener(rateLaterButtonClickListener: RateDialogClickListener) =
            apply {
                DialogOptions.rateLaterButton.rateDialogClickListener =
                    rateLaterButtonClickListener
            }

        fun showRateNeverButton(
            @StringRes rateNeverButtonTextId: Int = R.string.rating_dialog_button_rate_never,
            rateNeverButtonClickListener: RateDialogClickListener? = null
        ) = apply {
            DialogOptions.rateNeverButton =
                RateButton(rateNeverButtonTextId, rateNeverButtonClickListener)
            RatingLogger.debug("Show rate never button.")
        }

        // rating dialog overview
        fun setTitleTextId(@StringRes titleTextId: Int) = apply {
            DialogOptions.titleTextId = titleTextId
        }

        fun setMessageTextId(@StringRes messageTextId: Int) = apply {
            DialogOptions.messageTextId = messageTextId
        }

        fun setConfirmButtonTextId(@StringRes confirmButtonTextId: Int) = apply {
            DialogOptions.confirmButton.textId = confirmButtonTextId
        }

        fun setConfirmButtonClickListener(confirmButtonClickListener: ConfirmButtonClickListener) =
            apply {
                DialogOptions.confirmButton.confirmButtonClickListener = confirmButtonClickListener
            }

        fun setShowOnlyFullStars(showOnlyFullStars: Boolean) = apply {
            DialogOptions.showOnlyFullStars = showOnlyFullStars
        }

        // rating dialog store
        fun setStoreRatingTitleTextId(@StringRes storeRatingTitleTextId: Int) = apply {
            DialogOptions.storeRatingTitleTextId = storeRatingTitleTextId
        }

        fun setStoreRatingMessageTextId(@StringRes storeRatingMessageTextId: Int) = apply {
            DialogOptions.storeRatingMessageTextId = storeRatingMessageTextId
        }

        fun setRateNowButtonTextId(@StringRes rateNowButtonTextId: Int) = apply {
            DialogOptions.rateNowButton.textId = rateNowButtonTextId
        }

        fun overwriteRateNowButtonClickListener(rateNowButtonClickListener: RateDialogClickListener) =
            apply {
                DialogOptions.rateNowButton.rateDialogClickListener = rateNowButtonClickListener
            }

        fun setAdditionalRateNowButtonClickListener(additionalRateNowButtonClickListener: RateDialogClickListener) =
            apply {
                DialogOptions.additionalRateNowButtonClickListener =
                    additionalRateNowButtonClickListener
            }

        // rating dialog feedback
        fun setFeedbackTitleTextId(@StringRes feedbackTitleTextId: Int) = apply {
            DialogOptions.feedbackTitleTextId = feedbackTitleTextId
        }

        fun setNoFeedbackButtonTextId(@StringRes noFeedbackButtonTextId: Int) = apply {
            DialogOptions.noFeedbackButton.textId = noFeedbackButtonTextId
        }

        fun setNoFeedbackButtonClickListener(noFeedbackButtonClickListener: RateDialogClickListener) =
            apply {
                DialogOptions.noFeedbackButton.rateDialogClickListener =
                    noFeedbackButtonClickListener
            }

        // rating dialog mail feedback
        fun setMailFeedbackMessageTextId(@StringRes feedbackMailMessageTextId: Int) = apply {
            DialogOptions.mailFeedbackMessageTextId = feedbackMailMessageTextId
        }

        fun setMailSettingsForFeedbackDialog(mailSettings: MailSettings) = apply {
            DialogOptions.mailSettings = mailSettings
        }

        fun setMailFeedbackButtonTextId(@StringRes mailFeedbackButtonTextId: Int) = apply {
            DialogOptions.mailFeedbackButton.textId = mailFeedbackButtonTextId
        }

        fun overwriteMailFeedbackButtonClickListener(mailFeedbackButtonClickListener: RateDialogClickListener) =
            apply {
                DialogOptions.mailFeedbackButton.rateDialogClickListener =
                    mailFeedbackButtonClickListener
            }

        fun setAdditionalMailFeedbackButtonClickListener(additionalMailFeedbackButtonClickListener: RateDialogClickListener) =
            apply {
                DialogOptions.additionalMailFeedbackButtonClickListener =
                    additionalMailFeedbackButtonClickListener
            }

        // rating dialog custom feedback
        fun setUseCustomFeedback(useCustomFeedback: Boolean) = apply {
            DialogOptions.useCustomFeedback = useCustomFeedback
            RatingLogger.debug("Use custom feedback instead of mail feedback: $useCustomFeedback.")
        }

        fun setCustomFeedbackMessageTextId(@StringRes feedbackCustomMessageTextId: Int) = apply {
            DialogOptions.customFeedbackMessageTextId = feedbackCustomMessageTextId
        }

        fun setCustomFeedbackButtonTextId(@StringRes customFeedbackButtonTextId: Int) = apply {
            DialogOptions.customFeedbackButton.textId = customFeedbackButtonTextId
        }

        fun setCustomFeedbackButtonClickListener(customFeedbackButtonClickListener: CustomFeedbackButtonClickListener) =
            apply {
                DialogOptions.customFeedbackButton.customFeedbackButtonClickListener =
                    customFeedbackButtonClickListener
            }

        // other settings
        fun setRatingThreshold(ratingThreshold: RatingThreshold) = apply {
            DialogOptions.ratingThreshold = ratingThreshold
            RatingLogger.debug("Set rating threshold to ${ratingThreshold.ordinal / 2}.")
        }

        fun setCancelable(cancelable: Boolean) = apply {
            DialogOptions.cancelable = cancelable
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
            DialogOptions.customCondition = customCondition
            RatingLogger.debug("Custom condition set. This condition will be removed next time you call the Builder constructor.")
        }

        fun setCustomConditionToShowAgain(customConditionToShowAgain: () -> Boolean) = apply {
            DialogOptions.customConditionToShowAgain = customConditionToShowAgain
            RatingLogger.debug("Custom condition to show again set. This condition will be removed next time you call the Builder constructor.")
        }

        fun dontCountThisLaunch() = apply {
            DialogOptions.countAppLaunch = false
            RatingLogger.debug("countAppLaunch is now set to false. This setting will be reset next time you call the Builder constructor.")
        }

        fun setLoggingEnabled(isLoggingEnabled: Boolean) = apply {
            RatingLogger.isLoggingEnabled = isLoggingEnabled
        }

        fun setDebug(isDebug: Boolean) = apply {
            this.isDebug = isDebug
            RatingLogger.warn("Set debug to $isDebug. Don't use this for production.")
        }

        fun create(): DialogFragment = RateDialogFragment()

        fun showNow() {
            val rateDialogFragment = RateDialogFragment()
            rateDialogFragment.show(activity.supportFragmentManager, TAG)
        }

        fun showIfMeetsConditions() {
            if (DialogOptions.countAppLaunch) {
                RatingLogger.debug("App launch will be counted: countAppLaunch is true.")
                PreferenceUtil.increaseLaunchTimes(activity)
            } else {
                RatingLogger.info("App launch not counted this time: countAppLaunch has been set to false.")
            }

            if (isDebug || ConditionsChecker.shouldShowDialog(activity)) {
                RatingLogger.info("Show rating dialog now: Conditions met.")
                showNow()
            } else {
                RatingLogger.info("Don't show rating dialog: Conditions not met.")
            }
        }

        companion object {
            private val TAG = AppRating::class.java.simpleName
        }
    }
}
