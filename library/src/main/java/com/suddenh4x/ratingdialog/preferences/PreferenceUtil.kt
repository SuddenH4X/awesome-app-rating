package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.suddenh4x.ratingdialog.logging.RatingLogger

internal object PreferenceUtil {
    internal const val PREF_FILE_NAME = "awesome_app_rate"

    internal const val PREF_KEY_LAUNCH_TIMES = "launch_times"
    internal const val PREF_KEY_REMIND_TIMESTAMP = "timestamp"
    private const val PREF_KEY_MINIMUM_LAUNCH_TIMES = "minimum_launch_times"
    private const val PREF_KEY_MINIMUM_LAUNCH_TIMES_TO_SHOW_AGAIN =
        "minimum_launch_times_to_show_again"
    private const val PREF_KEY_MINIMUM_DAYS = "minimum_days"
    private const val PREF_KEY_MINIMUM_DAYS_TO_SHOW_AGAIN = "minimum_days_to_show_again"
    private const val PREF_KEY_DIALOG_AGREED = "dialog_agreed"
    private const val PREF_KEY_DIALOG_SHOW_LATER = "dialog_show_later"
    private const val PREF_KEY_DIALOG_DO_NOT_SHOW_AGAIN = "dialog_do_not_show_again"
    internal const val PREF_KEY_NUMBER_OF_LATER_BUTTON_CLICKS = "number_of_later_button_clicks"

    fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    fun increaseLaunchTimes(context: Context) {
        val launchTimes = getLaunchTimes(context)
        getPreferences(context).edit {
            putInt(PREF_KEY_LAUNCH_TIMES, launchTimes + 1)
        }
        RatingLogger.verbose("Increased launch times by 1. It's now ${launchTimes + 1}.")
    }

    fun getLaunchTimes(context: Context) = getPreferences(context).getInt(PREF_KEY_LAUNCH_TIMES, 0)

    fun setMinimumLaunchTimes(context: Context, minimumLaunchTimes: Int) {
        RatingLogger.verbose("Set minimum launch times to $minimumLaunchTimes.")
        getPreferences(context).edit {
            putInt(PREF_KEY_MINIMUM_LAUNCH_TIMES, minimumLaunchTimes)
        }
    }

    fun getMinimumLaunchTimes(context: Context) =
        getPreferences(context).getInt(PREF_KEY_MINIMUM_LAUNCH_TIMES, 5)

    fun setMinimumLaunchTimesToShowAgain(context: Context, minimumLaunchTimes: Int) {
        RatingLogger.verbose("Set minimum launch times to show the dialog again to $minimumLaunchTimes.")
        getPreferences(context).edit {
            putInt(PREF_KEY_MINIMUM_LAUNCH_TIMES_TO_SHOW_AGAIN, minimumLaunchTimes)
        }
    }

    fun getMinimumLaunchTimesToShowAgain(context: Context) =
        getPreferences(context).getInt(PREF_KEY_MINIMUM_LAUNCH_TIMES_TO_SHOW_AGAIN, 5)

    fun setMinimumDays(context: Context, minimumDays: Int) {
        RatingLogger.verbose("Set minimum days to $minimumDays.")
        getPreferences(context).edit {
            putInt(PREF_KEY_MINIMUM_DAYS, minimumDays)
        }
    }

    fun getMinimumDays(context: Context) = getPreferences(context).getInt(PREF_KEY_MINIMUM_DAYS, 3)

    fun setMinimumDaysToShowAgain(context: Context, minimumDays: Int) {
        RatingLogger.verbose("Set minimum days to show the dialog again to $minimumDays.")
        getPreferences(context).edit {
            putInt(PREF_KEY_MINIMUM_DAYS_TO_SHOW_AGAIN, minimumDays)
        }
    }

    fun getMinimumDaysToShowAgain(context: Context) =
        getPreferences(context).getInt(PREF_KEY_MINIMUM_DAYS_TO_SHOW_AGAIN, 14)

    fun onLaterButtonClicked(context: Context) {
        RatingLogger.verbose("Update remind timestamp because later button was clicked. Set launch times to 0.")
        getPreferences(context).edit {
            putLong(PREF_KEY_REMIND_TIMESTAMP, System.currentTimeMillis())
            putInt(PREF_KEY_LAUNCH_TIMES, 0)
            putBoolean(PREF_KEY_DIALOG_SHOW_LATER, true)
        }
        increaseNumberOfLaterButtonClicks(context)
    }

    fun getRemindTimestamp(context: Context): Long {
        val remindTimestamp = getPreferences(context).getLong(PREF_KEY_REMIND_TIMESTAMP, -1)
        if (remindTimestamp == -1L) {
            return setInitialRemindTimestamp(context)
        }
        return remindTimestamp
    }

    private fun setInitialRemindTimestamp(context: Context): Long {
        RatingLogger.debug("First app start. Set initial remind timestamp.")
        val currentTime = System.currentTimeMillis()
        getPreferences(context).edit {
            putLong(PREF_KEY_REMIND_TIMESTAMP, currentTime)
        }
        return currentTime
    }

    fun setDialogAgreed(context: Context) {
        RatingLogger.debug("Set dialog agreed.")
        getPreferences(context).edit {
            putBoolean(PREF_KEY_DIALOG_AGREED, true)
        }
    }

    fun isDialogAgreed(context: Context) =
        getPreferences(context).getBoolean(PREF_KEY_DIALOG_AGREED, false)

    fun wasLaterButtonClicked(context: Context) =
        getPreferences(context).getBoolean(PREF_KEY_DIALOG_SHOW_LATER, false)

    fun increaseNumberOfLaterButtonClicks(context: Context) {
        val numberOfLaterButtonClicks = getNumberOfLaterButtonClicks(context)
        getPreferences(context).edit {
            putInt(PREF_KEY_NUMBER_OF_LATER_BUTTON_CLICKS, numberOfLaterButtonClicks + 1)
        }
        RatingLogger.verbose("Increased number of later button clicks by 1. It's now ${numberOfLaterButtonClicks + 1}.")
    }

    fun getNumberOfLaterButtonClicks(context: Context) =
        getPreferences(context).getInt(PREF_KEY_NUMBER_OF_LATER_BUTTON_CLICKS, 0)

    fun setDoNotShowAgain(context: Context) {
        RatingLogger.debug("Set do not show again.")
        getPreferences(context).edit {
            putBoolean(PREF_KEY_DIALOG_DO_NOT_SHOW_AGAIN, true)
        }
    }

    fun isDoNotShowAgain(context: Context) =
        getPreferences(context).getBoolean(PREF_KEY_DIALOG_DO_NOT_SHOW_AGAIN, false)

    fun reset(context: Context) {
        RatingLogger.warn("Clearing all settings.")
        getPreferences(context).edit {
            clear()
        }
    }
}
