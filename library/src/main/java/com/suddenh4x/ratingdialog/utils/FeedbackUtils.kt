package com.suddenh4x.ratingdialog.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.suddenh4x.ratingdialog.R
import com.suddenh4x.ratingdialog.enums.AppStore
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.MailSettings

internal object FeedbackUtils {
    internal const val GOOGLE_PLAY_WEB_URL = "https://play.google.com/store/apps/details?id="
    internal const val GOOGLE_PLAY_IN_APP_URL = "market://details?id="
    internal const val AMAZON_STORE_WEB_URL = "http://www.amazon.com/gp/mas/dl/android?p="
    internal const val AMAZON_STORE_IN_APP_URL = "amzn://apps/android?p="
    internal const val URI_SCHEME_MAIL_TO = "mailto:"

    private fun isAmazonDevice(): Boolean {
        return Build.MANUFACTURER.equals("Amazon", ignoreCase = true)
    }

    fun openStoreListing(context: Context, store: AppStore = AppStore.AUTO) {
        when (store) {
            AppStore.GOOGLE_PLAYSTORE -> openPlayStoreListing(context)
            AppStore.AMAZON_APPSTORE -> openAmazonStoreListing(context)
            AppStore.AUTO -> {
                if (isAmazonDevice())
                    openAmazonStoreListing(context)
                else
                    openPlayStoreListing(context)
            }
        }
    }

    private fun openAmazonStoreListing(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AMAZON_STORE_IN_APP_URL + context.packageName),
                ),
            )
        } catch (activityNotFoundException: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AMAZON_STORE_WEB_URL + context.packageName),
                ),
            )
        }
    }

    private fun openPlayStoreListing(context: Context) {
        try {
            val uri = Uri.parse(GOOGLE_PLAY_IN_APP_URL + context.packageName)
            RatingLogger.info(context.getString(R.string.rating_dialog_log_feedback_utils_open_rating_url, uri))
            val googlePlayIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(googlePlayIntent)
        } catch (activityNotFoundException: ActivityNotFoundException) {
            RatingLogger.info(context.getString(R.string.rating_dialog_log_feedback_utils_play_store_not_found))
            val uri = Uri.parse(GOOGLE_PLAY_WEB_URL + context.packageName)
            RatingLogger.info(context.getString(R.string.rating_dialog_log_feedback_utils_open_rating_url_web, uri))
            val googlePlayIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(googlePlayIntent)
        }
    }

    fun openMailFeedback(context: Context, settings: MailSettings) {
        val mailIntent: Intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(URI_SCHEME_MAIL_TO)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(settings.mailAddress))
            putExtra(Intent.EXTRA_SUBJECT, settings.subject)
            putExtra(Intent.EXTRA_TEXT, settings.text)
        }

        if (mailIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mailIntent)
            RatingLogger.info(context.getString(R.string.rating_dialog_log_feedback_utils_open_mail_app))
        } else {
            val errorMessage = settings.errorToastMessage
                ?: context.getString(R.string.rating_dialog_feedback_mail_no_mail_error)
            RatingLogger.error(context.getString(R.string.rating_dialog_log_feedback_utils_mail_app_not_found))
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}
