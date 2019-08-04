package com.suddenh4x.ratingdialog.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.core.content.ContextCompat.startActivity
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
    private val TAG = DialogManager::class.java.simpleName
    private var rating: Float = -1f

    internal fun createRatingOverviewDialog(activity: FragmentActivity, dialogOptions: DialogOptions): AlertDialog {
        RatingLogger.debug("Creating rating overview dialog.")
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingOverviewDialogView = inflater.inflate(R.layout.dialog_rating_overview, null)
        initializeRatingDialogIcon(activity, ratingOverviewDialogView, dialogOptions)
        ratingOverviewDialogView.titleTextView.setText(dialogOptions.titleTextId)
        showOverviewMessage(dialogOptions, ratingOverviewDialogView.messageTextView)

        builder.apply {
            setView(ratingOverviewDialogView)

            setPositiveButton(dialogOptions.confirmButtonTextId) { _, _ ->
                when {
                    rating >= dialogOptions.ratingThreshold.toFloat() -> {
                        RatingLogger.info("Above threshold. Showing rating store dialog.")
                        val rateDialogFragment = RateDialogFragment()
                        rateDialogFragment.arguments =
                            Bundle().apply { putSerializable(ARG_DIALOG_TYPE, DialogType.RATING_STORE) }
                        rateDialogFragment.show(activity.supportFragmentManager, TAG)
                    }
                    dialogOptions.useCustomFeedback -> {
                        RatingLogger.info(
                            "Below threshold and custom feedback is enabled. Showing custom feedback dialog."
                        )
                        val rateDialogFragment = RateDialogFragment()
                        rateDialogFragment.arguments =
                            Bundle().apply { putSerializable(ARG_DIALOG_TYPE, DialogType.FEEDBACK_CUSTOM) }
                        rateDialogFragment.show(activity.supportFragmentManager, TAG)
                    }
                    else -> {
                        RatingLogger.info(
                            "Below threshold and custom feedback is disabled. Showing mail feedback dialog."
                        )
                        val rateDialogFragment = RateDialogFragment()
                        rateDialogFragment.arguments =
                            Bundle().apply { putSerializable(ARG_DIALOG_TYPE, DialogType.FEEDBACK_MAIL) }
                        rateDialogFragment.show(activity.supportFragmentManager, TAG)
                    }
                }
            }
            initializeRateLaterButton(activity, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(activity, dialogOptions.rateNeverButton, this)
        }

        return builder.create().also { dialog -> initRatingBar(ratingOverviewDialogView, dialog) }
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

    private fun initRatingBar(customRatingDialogView: View, dialog: AlertDialog) {
        customRatingDialogView.ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                DialogManager.rating = rating
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            }
        disablePositiveButtonWhenDialogShows(dialog)
    }

    internal fun createRatingStoreDialog(context: Context, dialogOptions: DialogOptions): AlertDialog {
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

                    button.rateDialogClickListener?.onClick()
                        ?: RatingLogger.error("Rate button has no click listener. Nothing happens.")
                }
            }
            initializeRateLaterButton(context, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(context, dialogOptions.rateNeverButton, this)
        }
        return builder.create()
    }

    internal fun createMailFeedbackDialog(context: Context, dialogOptions: DialogOptions): AlertDialog {
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
                        ?: openMailAppChooser(context, dialogOptions.mailSettings)
                }
            }
            initializeNoFeedbackButton(context, dialogOptions.noFeedbackButton, this)
        }
        return builder.create()
    }

    private fun openMailAppChooser(context: Context, mailSettings: MailSettings?) {
        mailSettings?.let { settings ->
            val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", settings.mailAddress, null)).apply {
                putExtra(Intent.EXTRA_SUBJECT, settings.subject)
                putExtra(Intent.EXTRA_TEXT, settings.text)
            }

            startActivity(context, Intent.createChooser(intent, settings.chooserTitle), null)
            RatingLogger.info("Open mail app chooser.")
        }
            ?: RatingLogger.error(
                "Mail feedback button has no click listener and mail settings are not set. Nothing happens."
            )
    }

    internal fun createCustomFeedbackDialog(context: Context, dialogOptions: DialogOptions): AlertDialog {
        RatingLogger.debug("Creating custom feedback dialog.")
        val builder = AlertDialog.Builder(context)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingCustomFeedbackDialogView = inflater.inflate(R.layout.dialog_rating_custom_feedback, null)
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
                    if (button.customFeedbackButtonClickListener != null) {
                        button.customFeedbackButtonClickListener.onClick(userFeedbackText)
                    } else {
                        RatingLogger.error("Custom feedback button has no click listener. Nothing happens.")
                    }
                }
            }
            initializeNoFeedbackButton(context, dialogOptions.noFeedbackButton, this)
        }
        return builder.create()
            .also { dialog -> initializeCustomFeedbackDialogButtonHandler(customFeedbackEditText, dialog) }
    }

    private fun initializeCustomFeedbackDialogButtonHandler(editText: EditText, dialog: AlertDialog) {
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
        rateLaterButton: RateButton?,
        dialogBuilder: AlertDialog.Builder
    ) {
        rateLaterButton?.let { button ->
            dialogBuilder.setNeutralButton(button.textId) { _, _ ->
                RatingLogger.info("Rate later button clicked.")
                PreferenceUtil.updateRemindTimestamp(context)
                button.rateDialogClickListener?.onClick()
                    ?: RatingLogger.info("Rate later button has no click listener.")
            }
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
