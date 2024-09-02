package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.suddenh4x.ratingdialog.R
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
    private const val PREF_KEY_DIALOG_SHOW_LATER = "dialog_show_later"

    fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    fun increaseLaunchTimes(context: Context) {
        val launchTimes = getLaunchTimes(context)
        getPreferences(context).edit {
            putInt(PREF_KEY_LAUNCH_TIMES, launchTimes + 1)
        }
        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_preference_launch_times_increased, launchTimes + 1))
    }

    fun getLaunchTimes(context: Context) = getPreferences(context).getInt(PREF_KEY_LAUNCH_TIMES, 0)

    fun setMinimumLaunchTimes(context: Context, minimumLaunchTimes: Int) {
        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_preference_minimum_launch_times_set, minimumLaunchTimes))
        getPreferences(context).edit {
            putInt(PREF_KEY_MINIMUM_LAUNCH_TIMES, minimumLaunchTimes)
        }
    }

    fun getMinimumLaunchTimes(context: Context) =
        getPreferences(context).getInt(PREF_KEY_MINIMUM_LAUNCH_TIMES, 5)

    fun setMinimumLaunchTimesToShowAgain(context: Context, minimumLaunchTimes: Int) {
        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_preference_minimum_launch_times_to_show_again_set, minimumLaunchTimes))
        getPreferences(context).edit {
            putInt(PREF_KEY_MINIMUM_LAUNCH_TIMES_TO_SHOW_AGAIN, minimumLaunchTimes)
        }
    }

    fun getMinimumLaunchTimesToShowAgain(context: Context) =
        getPreferences(context).getInt(PREF_KEY_MINIMUM_LAUNCH_TIMES_TO_SHOW_AGAIN, 5)

    fun setMinimumDays(context: Context, minimumDays: Int) {
        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_preference_minimum_days_set, minimumDays))
        getPreferences(context).edit {
            putInt(PREF_KEY_MINIMUM_DAYS, minimumDays)
        }
    }

    fun getMinimumDays(context: Context) = getPreferences(context).getInt(PREF_KEY_MINIMUM_DAYS, 3)

    fun setMinimumDaysToShowAgain(context: Context, minimumDays: Int) {
        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_preference_minimum_days_to_show_again_set, minimumDays))
        getPreferences(context).edit {
            putInt(PREF_KEY_MINIMUM_DAYS_TO_SHOW_AGAIN, minimumDays)
        }
    }

    fun getMinimumDaysToShowAgain(context: Context) =
        getPreferences(context).getInt(PREF_KEY_MINIMUM_DAYS_TO_SHOW_AGAIN, 14)

    fun onGoogleInAppReviewFlowCompleted(context: Context) {
        RatingLogger.verbose(context.getString(R.string.rating_dialog_log_preference_in_app_review_completed))
        getPreferences(context).edit {
            putLong(PREF_KEY_REMIND_TIMESTAMP, System.currentTimeMillis())
            putInt(PREF_KEY_LAUNCH_TIMES, 0)
            putBoolean(PREF_KEY_DIALOG_SHOW_LATER, true)
        }
    }

    fun getRemindTimestamp(context: Context): Long {
        val remindTimestamp = getPreferences(context).getLong(PREF_KEY_REMIND_TIMESTAMP, -1)
        if (remindTimestamp == -1L) {
            return setInitialRemindTimestamp(context)
        }
        return remindTimestamp
    }

    private fun setInitialRemindTimestamp(context: Context): Long {
        RatingLogger.debug(context.getString(R.string.rating_dialog_log_preference_initial_timestamp_set))
        val currentTime = System.currentTimeMillis()
        getPreferences(context).edit {
            putLong(PREF_KEY_REMIND_TIMESTAMP, currentTime)
        }
        return currentTime
    }

    fun wasLaterButtonClicked(context: Context) =
        getPreferences(context).getBoolean(PREF_KEY_DIALOG_SHOW_LATER, false)

    fun reset(context: Context) {
        RatingLogger.warn(context.getString(R.string.rating_dialog_log_preference_reset))
        getPreferences(context).edit {
            clear()
        }
    }
}
