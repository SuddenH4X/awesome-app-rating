package com.suddenh4x.ratingdialog.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.suddenh4x.ratingdialog.R
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.MailSettings

internal object FeedbackUtils {
    private const val GOOGLE_PLAY_WEB_URL = "https://play.google.com/store/apps/details?id="
    private const val GOOGLE_PLAY_IN_APP_URL = "market://details?id="
    private const val URI_SCHEME_MAIL_TO = "mailto:"

    fun openPlayStoreListing(context: Context) {
        try {
            val uri = Uri.parse(GOOGLE_PLAY_IN_APP_URL + context.packageName)
            RatingLogger.info("Open rating url (in app): $uri.")
            val googlePlayIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(googlePlayIntent)
        } catch (activityNotFoundException: ActivityNotFoundException) {
            RatingLogger.info("Google Play Store was not found on this device. Calling web url now.")
            val uri = Uri.parse(GOOGLE_PLAY_WEB_URL + context.packageName)
            RatingLogger.info("Open rating url (web): $uri.")
            val googlePlayIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(googlePlayIntent)
        }
    }

    fun openMailFeedback(context: Context, settings: MailSettings) {
        val mailIntent: Intent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("$URI_SCHEME_MAIL_TO ${settings.mailAddress}")
            putExtra(Intent.EXTRA_SUBJECT, settings.subject)
            putExtra(Intent.EXTRA_TEXT, settings.text)
        }

        if (mailIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mailIntent)
            RatingLogger.info("Open mail app.")
        } else {
            val errorMessage = settings.errorToastMessage
                ?: context.getString(R.string.rating_dialog_feedback_mail_no_mail_error)
            RatingLogger.error("No mail app is installed. Showing error toast now.")
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}