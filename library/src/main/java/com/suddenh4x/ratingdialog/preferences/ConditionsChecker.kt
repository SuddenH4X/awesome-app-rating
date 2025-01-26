package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import com.suddenh4x.ratingdialog.R
import com.suddenh4x.ratingdialog.dialog.DialogOptions
import com.suddenh4x.ratingdialog.logging.RatingLogger
import java.util.Date
import java.util.concurrent.TimeUnit

internal object ConditionsChecker {

    fun shouldShowDialog(
        context: Context,
        dialogOptions: DialogOptions,
    ): Boolean {
        RatingLogger.info(context.getString(R.string.rating_dialog_log_conditions_checker_checking))
        val isDialogAgreed = PreferenceUtil.isDialogAgreed(context)
        val isDoNotShowAgain = PreferenceUtil.isDoNotShowAgain(context)
        val remindTimestamp = PreferenceUtil.getRemindTimestamp(context)
        val wasLaterButtonClicked = PreferenceUtil.wasLaterButtonClicked(context)
        val currentTimestamp = System.currentTimeMillis()
        val daysBetween = calculateDaysBetween(Date(remindTimestamp), Date(currentTimestamp))

        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_conditions_checker_dialog_agreed, isDialogAgreed))
        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_conditions_checker_dont_show_again, isDoNotShowAgain))

        if (wasLaterButtonClicked) {
            RatingLogger.debug(context.getString(R.string.rating_dialog_log_conditions_checker_show_later_button_clicked))
            RatingLogger.verbose(context.getString(R.string.rating_dialog_log_conditions_checker_later_button_days_between, daysBetween))
            if (!checkCustomConditionToShowAgain(context, dialogOptions)) return false

            return (
                !isDialogAgreed && !isDoNotShowAgain &&
                    daysBetween >= PreferenceUtil.getMinimumDaysToShowAgain(context) &&
                    PreferenceUtil.getLaunchTimes(context) >=
                    PreferenceUtil.getMinimumLaunchTimesToShowAgain(context)
            )
        }

        if (!checkCustomCondition(context, dialogOptions)) return false

        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_conditions_checker_days_between, daysBetween))
        RatingLogger.debug(context.getString(R.string.rating_dialog_log_conditions_checker_later_button_not_clicked))
        return (
            !isDialogAgreed && !isDoNotShowAgain &&
                daysBetween >= PreferenceUtil.getMinimumDays(context) &&
                PreferenceUtil.getLaunchTimes(context) >=
                PreferenceUtil.getMinimumLaunchTimes(context)
        )
    }

    internal fun calculateDaysBetween(
        d1: Date,
        d2: Date,
    ): Long {
        return TimeUnit.MILLISECONDS.toDays(d2.time - d1.time)
    }

    private fun checkCustomCondition(
        context: Context,
        dialogOptions: DialogOptions,
    ): Boolean {
        dialogOptions.customCondition?.let { condition ->
            val conditionResult = condition()
            RatingLogger.info(context.getString(R.string.rating_dialog_log_conditions_checker_custom_condition, conditionResult))
            return conditionResult
        } ?: RatingLogger.debug(context.getString(R.string.rating_dialog_log_conditions_checker_no_custom_condition))
        return true
    }

    private fun checkCustomConditionToShowAgain(
        context: Context,
        dialogOptions: DialogOptions,
    ): Boolean {
        dialogOptions.customConditionToShowAgain?.let { condition ->
            val conditionResult = condition()
            RatingLogger.info(context.getString(R.string.rating_dialog_log_conditions_checker_custom_condition_to_show_again, conditionResult))
            return conditionResult
        } ?: RatingLogger.debug(context.getString(R.string.rating_dialog_log_conditions_checker_no_custom_condition_to_show_again))
        return true
    }
}
