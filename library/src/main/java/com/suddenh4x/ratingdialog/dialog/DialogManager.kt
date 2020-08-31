package com.suddenh4x.ratingdialog.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.suddenh4x.ratingdialog.R
import com.suddenh4x.ratingdialog.buttons.RateButton
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.preferences.toFloat
import com.suddenh4x.ratingdialog.utils.FeedbackUtils
import kotlinx.android.synthetic.main.dialog_rating_custom_feedback.view.*
import kotlinx.android.synthetic.main.dialog_rating_overview.view.*
import kotlinx.android.synthetic.main.dialog_rating_overview.view.imageView
import kotlinx.android.synthetic.main.dialog_rating_store.view.*

@SuppressLint("InflateParams")
internal object DialogManager {
    private val TAG = DialogManager::class.java.simpleName
    private var rating: Float = -1f

    internal fun createRatingOverviewDialog(
        activity: FragmentActivity,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating rating overview dialog.")
        val builder = getDialogBuilder(activity)

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
                        showRatingDialog(dialogOptions, DialogType.RATING_STORE, activity)
                    }
                    dialogOptions.useCustomFeedback -> {
                        RatingLogger.info(
                            "Below threshold and custom feedback is enabled. Showing custom feedback dialog."
                        )
                        PreferenceUtil.setDialogAgreed(context)
                        showRatingDialog(dialogOptions, DialogType.FEEDBACK_CUSTOM, activity)
                    }
                    else -> {
                        RatingLogger.info(
                            "Below threshold and custom feedback is disabled. Showing mail feedback dialog."
                        )
                        PreferenceUtil.setDialogAgreed(context)
                        showRatingDialog(dialogOptions, DialogType.FEEDBACK_MAIL, activity)
                    }
                }
            }
            initializeRateLaterButton(activity, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(activity, dialogOptions, this)
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

    private fun showRatingDialog(
        dialogOptions: DialogOptions,
        dialogType: DialogType,
        activity: FragmentActivity
    ) {
        RateDialogFragment.newInstance(dialogOptions, dialogType)
            .show(activity.supportFragmentManager, TAG)
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
        val builder = getDialogBuilder(context)

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
                        ?: run {
                            RatingLogger.info("Default rate now button click listener called.")
                            FeedbackUtils.openPlayStoreListing(context)
                        }
                    dialogOptions.additionalRateNowButtonClickListener?.onClick()
                        ?: RatingLogger.info("Additional rate now button click listener not set.")
                }
            }
            initializeRateLaterButton(context, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(context, dialogOptions, this)
        }
        return builder.create()
    }

    internal fun createMailFeedbackDialog(
        context: Context,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating mail feedback dialog.")
        val builder = getDialogBuilder(context)

        builder.apply {
            setTitle(dialogOptions.feedbackTitleTextId)
            setMessage(dialogOptions.mailFeedbackMessageTextId)
            setCancelable(dialogOptions.cancelable)

            dialogOptions.mailFeedbackButton.let { button ->
                setPositiveButton(button.textId) { _, _ ->
                    RatingLogger.info("Mail feedback button clicked.")

                    button.rateDialogClickListener?.onClick() ?: openMailFeedback(
                        context,
                        dialogOptions.mailSettings
                    )

                    dialogOptions.additionalMailFeedbackButtonClickListener?.onClick()
                        ?: RatingLogger.info("Additional mail feedback button click listener not set.")
                }
            }
            initializeNoFeedbackButton(context, dialogOptions.noFeedbackButton, this)
        }
        return builder.create()
    }

    private fun openMailFeedback(context: Context, mailSettings: MailSettings?) {
        if (mailSettings != null) {
            FeedbackUtils.openMailFeedback(context, mailSettings)
        } else {
            RatingLogger.error(
                "Mail feedback button has no click listener and mail settings hasn't been set. Nothing happens."
            )
        }
    }

    internal fun createCustomFeedbackDialog(
        context: Context,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating custom feedback dialog.")
        val builder = getDialogBuilder(context)

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
            PreferenceUtil.onLaterButtonClicked(context)
            rateLaterButton.rateDialogClickListener?.onClick()
                ?: RatingLogger.info("Rate later button has no click listener.")
        }
    }

    private fun initializeRateNeverButton(
        context: Context,
        dialogOptions: DialogOptions,
        dialogBuilder: AlertDialog.Builder
    ) {
        val countOfLaterButtonClicksToShowNeverButton =
            dialogOptions.countOfLaterButtonClicksToShowNeverButton
        val numberOfLaterButtonClicks = PreferenceUtil.getNumberOfLaterButtonClicks(context)
        RatingLogger.debug("Rate later button was clicked $numberOfLaterButtonClicks times.")
        if (countOfLaterButtonClicksToShowNeverButton > numberOfLaterButtonClicks) {
            RatingLogger.info(
                "Less than $countOfLaterButtonClicksToShowNeverButton later " +
                    "button clicks. Rate never button won't be displayed."
            )
            return
        }

        dialogOptions.rateNeverButton?.let { button ->
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
                button.rateDialogClickListener?.onClick()
                    ?: RatingLogger.info("No feedback button has no click listener.")
            }
        }
    }

    private fun getDialogBuilder(context: Context): AlertDialog.Builder {
        return try {
            MaterialAlertDialogBuilder(context)
        } catch (ex: IllegalArgumentException) {
            RatingLogger.debug("This app doesn't use a MaterialComponents theme. Using normal AlertDialog instead.")
            AlertDialog.Builder(context)
        }
    }
}
