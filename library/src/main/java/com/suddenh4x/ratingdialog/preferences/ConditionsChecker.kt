package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import com.suddenh4x.ratingdialog.dialog.DialogOptions
import com.suddenh4x.ratingdialog.logging.RatingLogger
import java.util.Date
import java.util.concurrent.TimeUnit

internal object ConditionsChecker {

    fun shouldShowDialog(context: Context, dialogOptions: DialogOptions): Boolean {
        RatingLogger.info("Checking conditions.")
        val isDialogAgreed = PreferenceUtil.isDialogAgreed(context)
        val isDoNotShowAgain = PreferenceUtil.isDoNotShowAgain(context)
        val remindTimestamp = PreferenceUtil.getRemindTimestamp(context)
        val wasLaterButtonClicked = PreferenceUtil.wasLaterButtonClicked(context)
        val currentTimestamp = System.currentTimeMillis()
        val daysBetween = calculateDaysBetween(Date(remindTimestamp), Date(currentTimestamp))

        RatingLogger.verbose("Is dialog agreed: $isDialogAgreed.")
        RatingLogger.verbose("Do not show again: $isDoNotShowAgain.")

        if (wasLaterButtonClicked) {
            RatingLogger.debug("Show later button has already been clicked.")
            RatingLogger.verbose("Days between later button click and now: $daysBetween.")
            if (!checkCustomConditionToShowAgain(dialogOptions)) return false

            return (
                !isDialogAgreed && !isDoNotShowAgain &&
                    daysBetween >= PreferenceUtil.getMinimumDaysToShowAgain(context) &&
                    PreferenceUtil.getLaunchTimes(context) >=
                    PreferenceUtil.getMinimumLaunchTimesToShowAgain(context)
                )
        }

        if (!checkCustomCondition(dialogOptions)) return false

        RatingLogger.verbose("Days between first app start and now: $daysBetween.")
        RatingLogger.debug("Show later button hasn't been clicked until now.")
        return (
            !isDialogAgreed && !isDoNotShowAgain &&
                daysBetween >= PreferenceUtil.getMinimumDays(context) &&
                PreferenceUtil.getLaunchTimes(context) >=
                PreferenceUtil.getMinimumLaunchTimes(context)
            )
    }

    internal fun calculateDaysBetween(d1: Date, d2: Date): Long {
        return TimeUnit.MILLISECONDS.toDays(d2.time - d1.time)
    }

    private fun checkCustomCondition(dialogOptions: DialogOptions): Boolean {
        dialogOptions.customCondition?.let { condition ->
            val conditionResult = condition()
            RatingLogger.info("Custom condition found. Condition result is: $conditionResult.")
            return conditionResult
        } ?: RatingLogger.debug("No custom condition was set.")
        return true
    }

    private fun checkCustomConditionToShowAgain(dialogOptions: DialogOptions): Boolean {
        dialogOptions.customConditionToShowAgain?.let { condition ->
            val conditionResult = condition()
            RatingLogger.info("Custom condition to show again found. Condition result is: $conditionResult.")
            return conditionResult
        } ?: RatingLogger.debug("No custom condition to show again was set.")
        return true
    }
}
