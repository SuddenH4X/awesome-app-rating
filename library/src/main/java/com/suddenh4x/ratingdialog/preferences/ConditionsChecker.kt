package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import com.suddenh4x.ratingdialog.logging.RatingLogger
import java.util.Date
import java.util.concurrent.TimeUnit

internal object ConditionsChecker {

    fun shouldShowDialog(context: Context): Boolean {
        RatingLogger.info("Checking conditions.")
        val isDialogAgreed = PreferenceUtil.isDialogAgreed(context)
        val isDoNotShowAgain = PreferenceUtil.isDoNotShowAgain(context)
        val remindTimestamp = PreferenceUtil.getRemindTimestamp(context)
        val showDialogLater = PreferenceUtil.shouldShowDialogLater(context)
        val currentTimestamp = System.currentTimeMillis()
        val daysBetween = calculateDaysBetween(Date(remindTimestamp), Date(currentTimestamp))

        RatingLogger.verbose("Is dialog agreed: $isDialogAgreed.")
        RatingLogger.verbose("Do not show again: $isDoNotShowAgain.")
        RatingLogger.verbose("Days between later button click and now: $daysBetween.")

        if (showDialogLater) {
            RatingLogger.debug("Show later button has already been clicked.")
            return (!isDialogAgreed &&
                !isDoNotShowAgain &&
                daysBetween >= PreferenceUtil.getMinimumDaysToShowAgain(context) &&
                (PreferenceUtil.getLaunchTimes(context) >= PreferenceUtil.getMinimumLaunchTimesToShowAgain(context)))
        }

        RatingLogger.debug("Show later button hasn't been clicked until now.")
        return (!isDialogAgreed &&
            !isDoNotShowAgain &&
            daysBetween >= PreferenceUtil.getMinimumDays(context) &&
            (PreferenceUtil.getLaunchTimes(context) >= PreferenceUtil.getMinimumLaunchTimes(context)))
    }

    internal fun calculateDaysBetween(d1: Date, d2: Date): Long {
        return TimeUnit.MILLISECONDS.toDays(d2.time - d1.time)
    }
}
