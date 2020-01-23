package com.suddenh4x.ratingdialog.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.suddenh4x.ratingdialog.R
import com.suddenh4x.ratingdialog.buttons.RateButton
import com.suddenh4x.ratingdialog.dialog.RateDialogFragment.Companion.ARG_DIALOG_TYPE
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.preferences.toFloat
import kotlinx.android.synthetic.main.dialog_rating_custom_feedback.view.*
import kotlinx.android.synthetic.main.dialog_rating_overview.view.*
import kotlinx.android.synthetic.main.dialog_rating_overview.view.imageView
import kotlinx.android.synthetic.main.dialog_rating_store.view.*

@SuppressLint("InflateParams")
internal object DialogManager {
    private const val GOOGLE_PLAY_WEB_URL = "https://play.google.com/store/apps/details?id="
    private const val GOOGLE_PLAY_IN_APP_URL = "market://details?id="
    private val TAG = DialogManager::class.java.simpleName
    private var rating: Float = -1f

    internal fun createRatingOverviewDialog(
        activity: FragmentActivity,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating rating overview dialog.")
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingOverviewDialogView = inflater.inflate(R.layout.dialog_rating_overview, null)
        initializeRatingDialogIcon(activity, ratingOverviewDialogView, dialogOptions)
        ratingOverviewDialogView.titleTextView.setText(dialogOptions.titleTextId)
        showOverviewMessage(dialogOptions, ratingOverviewDialogView.messageTextView)

        builder.apply {
            setView(ratingOverviewDialogView)

            setPositiveButton(dialogOptions.confirmButton.textId) { _, _ ->
                RatingLogger.debug("Confirm button clicked.")
                dialogOptions.confirmButton.confirmButtonClickListener?.onClick(rating)
                    ?: RatingLogger.info("Confirm button has no click listener.")

                when {
                    rating >= dialogOptions.ratingThreshold.toFloat() -> {
                        RatingLogger.info("Above threshold. Showing rating store dialog.")
                        showRatingDialog(DialogType.RATING_STORE, activity)
                    }
                    dialogOptions.useCustomFeedback -> {
                        RatingLogger.info(
                            "Below threshold and custom feedback is enabled. Showing custom feedback dialog."
                        )
                        showRatingDialog(DialogType.FEEDBACK_CUSTOM, activity)
                    }
                    else -> {
                        RatingLogger.info(
                            "Below threshold and custom feedback is disabled. Showing mail feedback dialog."
                        )
                        showRatingDialog(DialogType.FEEDBACK_MAIL, activity)
                    }
                }
            }
            initializeRateLaterButton(activity, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(activity, dialogOptions.rateNeverButton, this)
        }

        return builder.create().also { dialog ->
            initRatingBar(
                ratingOverviewDialogView,
                dialogOptions.showOnlyFullStars,
                dialog
            )
        }
    }

    @SuppressLint("ResourceType")
    private fun showOverviewMessage(dialogOptions: DialogOptions, messageTextView: TextView) {
        dialogOptions.messageTextId?.let { messageTextId ->
            messageTextView.apply {
                setText(messageTextId)
                visibility = View.VISIBLE
            }
        }
    }

    private fun showRatingDialog(dialogType: DialogType, activity: FragmentActivity) {
        val rateDialogFragment = RateDialogFragment()
        rateDialogFragment.arguments = Bundle().apply {
            putSerializable(
                ARG_DIALOG_TYPE,
                dialogType
            )
        }
        rateDialogFragment.show(activity.supportFragmentManager, TAG)
    }

    private fun initRatingBar(
        customRatingDialogView: View,
        showOnlyFullStars: Boolean,
        dialog: AlertDialog
    ) {
        customRatingDialogView.ratingBar.apply {
            if (showOnlyFullStars) {
                stepSize = 1f
            }
            onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                DialogManager.rating = rating
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            }
        }
        disablePositiveButtonWhenDialogShows(dialog)
    }

    internal fun createRatingStoreDialog(
        context: Context,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating store rating dialog.")
        val builder = AlertDialog.Builder(context)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingStoreDialogView = inflater.inflate(R.layout.dialog_rating_store, null)
        initializeRatingDialogIcon(context, ratingStoreDialogView, dialogOptions)
        ratingStoreDialogView.storeRatingTitleTextView.setText(dialogOptions.storeRatingTitleTextId)
        ratingStoreDialogView.storeRatingMessageTextView.setText(dialogOptions.storeRatingMessageTextId)

        builder.apply {
            setView(ratingStoreDialogView)
            setCancelable(dialogOptions.cancelable)

            dialogOptions.rateNowButton.let { button ->
                setPositiveButton(button.textId) { _, _ ->
                    RatingLogger.info("Rate button clicked.")
                    PreferenceUtil.setDialogAgreed(context)

                    button.rateDialogClickListener?.onClick() ?: openPlayStore(context)
                    dialogOptions.additionalRateNowButtonClickListener?.onClick()
                        ?: RatingLogger.info("Additional rate now button click listener not set.")
                }
            }
            initializeRateLaterButton(context, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(context, dialogOptions.rateNeverButton, this)
        }
        return builder.create()
    }

    internal fun createMailFeedbackDialog(
        context: Context,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating mail feedback dialog.")
        val builder = AlertDialog.Builder(context)

        builder.apply {
            setTitle(dialogOptions.feedbackTitleTextId)
            setMessage(dialogOptions.mailFeedbackMessageTextId)
            setCancelable(dialogOptions.cancelable)

            dialogOptions.mailFeedbackButton.let { button ->
                setPositiveButton(button.textId) { _, _ ->
                    RatingLogger.info("Mail feedback button clicked.")
                    PreferenceUtil.setDialogAgreed(context)

                    button.rateDialogClickListener?.onClick()
                        ?: openMailApp(context, dialogOptions.mailSettings)

                    dialogOptions.additionalMailFeedbackButtonClickListener?.onClick()
                        ?: RatingLogger.info("Additional mail feedback button click listener not set.")
                }
            }
            initializeNoFeedbackButton(context, dialogOptions.noFeedbackButton, this)
        }
        return builder.create()
    }

    private fun openMailApp(context: Context, mailSettings: MailSettings?) {
        mailSettings?.let { settings ->
            val mailIntent: Intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto: ${settings.mailAddress}")
                putExtra(Intent.EXTRA_SUBJECT, settings.subject)
                putExtra(Intent.EXTRA_TEXT, settings.text)
            }

            if (mailIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mailIntent)
                RatingLogger.info("Open mail app.")
            } else {
                val errorMessage = mailSettings.errorToastMessage
                    ?: context.getString(R.string.rating_dialog_feedback_mail_no_mail_error)
                RatingLogger.error("No mail app is installed. Showing error toast now.")
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        } ?: RatingLogger.error(
            "Mail feedback button has no click listener and mail settings are not set. Nothing happens."
        )
    }

    internal fun createCustomFeedbackDialog(
        context: Context,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating custom feedback dialog.")
        val builder = AlertDialog.Builder(context)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingCustomFeedbackDialogView =
            inflater.inflate(R.layout.dialog_rating_custom_feedback, null)
        val customFeedbackEditText = ratingCustomFeedbackDialogView.customFeedbackEditText
        ratingCustomFeedbackDialogView.customFeedbackTitleTextView.setText(dialogOptions.feedbackTitleTextId)
        customFeedbackEditText.setHint(dialogOptions.customFeedbackMessageTextId)

        builder.apply {
            setView(ratingCustomFeedbackDialogView)
            setCancelable(dialogOptions.cancelable)

            dialogOptions.customFeedbackButton.let { button ->
                setPositiveButton(button.textId) { _, _ ->
                    RatingLogger.info("Custom feedback button clicked.")
                    PreferenceUtil.setDialogAgreed(context)

                    val userFeedbackText = customFeedbackEditText.text.toString()
                    button.customFeedbackButtonClickListener?.onClick(userFeedbackText)
                        ?: RatingLogger.error("Custom feedback button has no click listener. Nothing happens.")
                }
            }
            initializeNoFeedbackButton(context, dialogOptions.noFeedbackButton, this)
        }
        return builder.create()
            .also { dialog ->
                initializeCustomFeedbackDialogButtonHandler(
                    customFeedbackEditText,
                    dialog
                )
            }
    }

    private fun initializeCustomFeedbackDialogButtonHandler(
        editText: EditText,
        dialog: AlertDialog
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = (count > 0)
            }
        })
    }

    private fun disablePositiveButtonWhenDialogShows(dialog: AlertDialog) {
        dialog.setOnShowListener { visibleDialog ->
            (visibleDialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
    }

    private fun openPlayStore(context: Context) {
        try {
            RatingLogger.info("Default rate now button click listener was called.")
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

    private fun initializeRatingDialogIcon(
        context: Context,
        customRatingDialogView: View,
        dialogOptions: DialogOptions
    ) {
        if (dialogOptions.iconDrawable != null) {
            RatingLogger.info("Use custom rating dialog icon.")
            customRatingDialogView.imageView.setImageDrawable(dialogOptions.iconDrawable)
        } else {
            RatingLogger.info("Use app icon for rating dialog.")
            val appIcon = context.packageManager.getApplicationIcon(context.applicationInfo)
            customRatingDialogView.imageView.setImageDrawable(appIcon)
        }
    }

    private fun initializeRateLaterButton(
        context: Context,
        rateLaterButton: RateButton,
        dialogBuilder: AlertDialog.Builder
    ) {
        dialogBuilder.setNeutralButton(rateLaterButton.textId) { _, _ ->
            RatingLogger.info("Rate later button clicked.")
            PreferenceUtil.updateRemindTimestamp(context)
            rateLaterButton.rateDialogClickListener?.onClick()
                ?: RatingLogger.info("Rate later button has no click listener.")
        }
    }

    private fun initializeRateNeverButton(
        context: Context,
        rateNeverButton: RateButton?,
        dialogBuilder: AlertDialog.Builder
    ) {
        rateNeverButton?.let { button ->
            dialogBuilder.setNegativeButton(button.textId) { _, _ ->
                RatingLogger.info("Rate never button clicked.")
                PreferenceUtil.setDoNotShowAgain(context)
                button.rateDialogClickListener?.onClick()
                    ?: RatingLogger.info("Rate never button has no click listener.")
            }
        }
    }

    private fun initializeNoFeedbackButton(
        context: Context,
        noFeedbackButton: RateButton,
        dialogBuilder: AlertDialog.Builder
    ) {
        noFeedbackButton.let { button ->
            dialogBuilder.setNegativeButton(button.textId) { _, _ ->
                RatingLogger.info("No feedback button clicked.")
                PreferenceUtil.setDialogAgreed(context)
                button.rateDialogClickListener?.onClick()
                    ?: RatingLogger.info("No feedback button has no click listener.")
            }
        }
    }
}
