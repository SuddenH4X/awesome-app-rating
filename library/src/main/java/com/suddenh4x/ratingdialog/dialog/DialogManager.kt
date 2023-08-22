package com.suddenh4x.ratingdialog.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.suddenh4x.ratingdialog.buttons.RateButton
import com.suddenh4x.ratingdialog.databinding.DialogRatingCustomFeedbackBinding
import com.suddenh4x.ratingdialog.databinding.DialogRatingOverviewBinding
import com.suddenh4x.ratingdialog.databinding.DialogRatingStoreBinding
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.preferences.toFloat
import com.suddenh4x.ratingdialog.utils.FeedbackUtils
import java.lang.IllegalStateException

@SuppressLint("InflateParams")
internal object DialogManager {
    private val TAG = DialogManager::class.java.simpleName
    private var rating: Float = -1f

    internal fun createRatingOverviewDialog(
        activity: FragmentActivity,
        dialogOptions: DialogOptions
    ): Dialog {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingOverviewDialogBinding = DialogRatingOverviewBinding.inflate(inflater)
        initializeRatingDialogIcon(activity, ratingOverviewDialogBinding.imageView, dialogOptions)
        ratingOverviewDialogBinding.titleTextView.setText(dialogOptions.titleTextId)
        showOverviewMessage(dialogOptions, ratingOverviewDialogBinding.messageTextView)

        val dialog: Dialog = if (dialogOptions.bottomSheet) {
            BottomSheetAlertDialog(activity, dialogOptions.customBottomSheetTheme).apply {
                setContentView(ratingOverviewDialogBinding.root)
            }
        } else {
            RatingLogger.debug("Creating rating overview dialog.")
            getDialogBuilder(
                activity,
                dialogOptions.customDialogTheme
            ).apply { setView(ratingOverviewDialogBinding.root) }.create()
        }.apply {
            val clickListener = { dialogInterface: DialogInterface, which: Int ->
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
                            "Below threshold and custom feedback is enabled. Showing custom feedback dialog.",
                        )
                        PreferenceUtil.setDialogAgreed(context)
                        showRatingDialog(dialogOptions, DialogType.FEEDBACK_CUSTOM, activity)
                    }

                    else -> {
                        RatingLogger.info(
                            "Below threshold and custom feedback is disabled. Showing mail feedback dialog.",
                        )
                        PreferenceUtil.setDialogAgreed(context)
                        showRatingDialog(dialogOptions, DialogType.FEEDBACK_MAIL, activity)
                    }
                }
                dialogInterface.dismiss()
            }
            if (this is AlertDialog) {
                setButton(
                    AlertDialog.BUTTON_POSITIVE,
                    context.getString(dialogOptions.confirmButton.textId),
                    clickListener
                )
            } else if (this is BottomSheetAlertDialog) {
                setButton(
                    AlertDialog.BUTTON_POSITIVE,
                    context.getString(dialogOptions.confirmButton.textId),
                    clickListener
                )
            }

            initializeRateLaterButton(activity, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(activity, dialogOptions, this)
            setCancelable(dialogOptions.cancelable)
            initRatingBar(
                ratingOverviewDialogBinding.ratingBar,
                dialogOptions.showOnlyFullStars,
                this,
            )
        }

        return dialog
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
        ratingBar: RatingBar,
        showOnlyFullStars: Boolean,
        dialog: Dialog
    ) {
        ratingBar.apply {
            if (showOnlyFullStars) {
                stepSize = 1f
            }
            onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                DialogManager.rating = rating
                getButton(dialog, AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
            }
        }
        disablePositiveButtonWhenDialogShows(dialog)
    }

    private fun getButton(dialog: DialogInterface, whichButton: Int): Button? {
        return when (dialog) {
            is AlertDialog -> dialog.getButton(whichButton)
            is BottomSheetAlertDialog -> dialog.getButton(whichButton)
            else -> throw IllegalStateException("Dialog must be either AlertDialog or BottomSheetAlertDialog")
        }
    }

    internal fun createRatingStoreDialog(
        context: Context,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating store rating dialog.")
        val builder = getDialogBuilder(context, dialogOptions.customDialogTheme)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingStoreDialogBinding = DialogRatingStoreBinding.inflate(inflater)
        initializeRatingDialogIcon(context, ratingStoreDialogBinding.imageView, dialogOptions)
        ratingStoreDialogBinding.storeRatingTitleTextView.setText(dialogOptions.storeRatingTitleTextId)
        ratingStoreDialogBinding.storeRatingMessageTextView.setText(dialogOptions.storeRatingMessageTextId)

        builder.apply {
            setView(ratingStoreDialogBinding.root)
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
        }

        return builder.create().apply {
            initializeRateLaterButton(context, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(context, dialogOptions, this)
        }
    }

    internal fun createMailFeedbackDialog(
        context: Context,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating mail feedback dialog.")
        val builder = getDialogBuilder(context, dialogOptions.customDialogTheme)

        builder.apply {
            setTitle(dialogOptions.feedbackTitleTextId)
            setMessage(dialogOptions.mailFeedbackMessageTextId)
            setCancelable(dialogOptions.cancelable)

            dialogOptions.mailFeedbackButton.let { button ->
                setPositiveButton(button.textId) { _, _ ->
                    RatingLogger.info("Mail feedback button clicked.")

                    button.rateDialogClickListener?.onClick() ?: openMailFeedback(
                        context,
                        dialogOptions.mailSettings,
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
                "Mail feedback button has no click listener and mail settings hasn't been set. Nothing happens.",
            )
        }
    }

    internal fun createCustomFeedbackDialog(
        context: Context,
        dialogOptions: DialogOptions
    ): AlertDialog {
        RatingLogger.debug("Creating custom feedback dialog.")
        val builder = getDialogBuilder(context, dialogOptions.customDialogTheme)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingCustomFeedbackDialogBinding = DialogRatingCustomFeedbackBinding.inflate(inflater)
        val customFeedbackEditText = ratingCustomFeedbackDialogBinding.customFeedbackEditText
        ratingCustomFeedbackDialogBinding.customFeedbackTitleTextView.setText(dialogOptions.feedbackTitleTextId)
        customFeedbackEditText.setHint(dialogOptions.customFeedbackMessageTextId)

        builder.apply {
            setView(ratingCustomFeedbackDialogBinding.root)
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
                    dialog,
                )
            }
    }

    private fun initializeCustomFeedbackDialogButtonHandler(
        editText: EditText,
        dialog: AlertDialog
    ) {
        editText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = (count > 0)
                }
            },
        )
    }

    private fun disablePositiveButtonWhenDialogShows(dialog: Dialog) {
        if (dialog is AlertDialog)
            dialog.setOnShowListener { visibleDialog ->
                getButton(visibleDialog, AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
            }
    }

    private fun initializeRatingDialogIcon(
        context: Context,
        imageView: ImageView,
        dialogOptions: DialogOptions
    ) {
        if (dialogOptions.iconDrawable != null) {
            RatingLogger.info("Use custom rating dialog icon.")
            imageView.setImageDrawable(dialogOptions.iconDrawable)
        } else {
            RatingLogger.info("Use app icon for rating dialog.")
            val appIcon = context.packageManager.getApplicationIcon(context.applicationInfo)
            imageView.setImageDrawable(appIcon)
        }
    }

    private fun initializeRateLaterButton(
        context: Context,
        rateLaterButton: RateButton,
        dialog: Dialog
    ) {
        val clickListener = { dialogInterface: DialogInterface, _: Int ->
            RatingLogger.info("Rate later button clicked.")
            PreferenceUtil.onLaterButtonClicked(context)
            rateLaterButton.rateDialogClickListener?.onClick()
                ?: RatingLogger.info("Rate later button has no click listener.")
            dialogInterface.dismiss()
        }
        if (dialog is AlertDialog) {
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(rateLaterButton.textId), clickListener)
        } else if (dialog is BottomSheetAlertDialog) {
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(rateLaterButton.textId), clickListener)
        }
    }

    private fun initializeRateNeverButton(
        context: Context,
        dialogOptions: DialogOptions,
        dialogBuilder: Dialog
    ) {
        val countOfLaterButtonClicksToShowNeverButton =
            dialogOptions.countOfLaterButtonClicksToShowNeverButton
        val numberOfLaterButtonClicks = PreferenceUtil.getNumberOfLaterButtonClicks(context)
        RatingLogger.debug("Rate later button was clicked $numberOfLaterButtonClicks times.")
        if (countOfLaterButtonClicksToShowNeverButton > numberOfLaterButtonClicks) {
            RatingLogger.info(
                "Less than $countOfLaterButtonClicksToShowNeverButton later " +
                        "button clicks. Rate never button won't be displayed.",
            )
            return
        }

        dialogOptions.rateNeverButton?.let { button ->
            val clickListener = { dialogInterface: DialogInterface, _: Int ->
                RatingLogger.info("Rate never button clicked.")
                PreferenceUtil.setDoNotShowAgain(context)
                button.rateDialogClickListener?.onClick()
                    ?: RatingLogger.info("Rate never button has no click listener.")
                dialogInterface.dismiss()
            }
            if (dialogBuilder is AlertDialog) {
                dialogBuilder.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(button.textId), clickListener)
            } else if (dialogBuilder is BottomSheetAlertDialog) {
                dialogBuilder.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(button.textId), clickListener)
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

    private fun getDialogBuilder(context: Context, theme: Int): AlertDialog.Builder {
        return try {
            MaterialAlertDialogBuilder(context, theme)
        } catch (ex: IllegalArgumentException) {
            RatingLogger.debug("This app doesn't use a MaterialComponents theme. Using normal AlertDialog instead.")
            AlertDialog.Builder(context, theme)
        }
    }
}
