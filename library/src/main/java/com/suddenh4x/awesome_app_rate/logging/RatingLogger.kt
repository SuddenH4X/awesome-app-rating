package com.suddenh4x.awesome_app_rate.logging

import android.util.Log

object RatingLogger {
    private const val TAG = "awesome_app_rate"
    var loggingEnabled = true

    internal fun verbose(logMessage: String) {
        if (loggingEnabled) {
            Log.v(TAG, logMessage)
        }
    }

    internal fun debug(logMessage: String) {
        if (loggingEnabled) {
            Log.d(TAG, logMessage)
        }
    }

    internal fun info(logMessage: String) {
        if (loggingEnabled) {
            Log.i(TAG, logMessage)
        }
    }

    internal fun warn(logMessage: String) {
        if (loggingEnabled) {
            Log.w(TAG, logMessage)
        }
    }

    internal fun error(logMessage: String) {
        if (loggingEnabled) {
            Log.e(TAG, logMessage)
        }
    }
}
