package com.suddenh4x.ratingdialog.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.suddenh4x.ratingdialog.R
import com.suddenh4x.ratingdialog.buttons.RateButton
import com.suddenh4x.ratingdialog.databinding.DialogRatingCustomFeedbackBinding
import com.suddenh4x.ratingdialog.databinding.DialogRatingOverviewBinding
import com.suddenh4x.ratingdialog.databinding.DialogRatingStoreBinding
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import com.suddenh4x.ratingdialog.preferences.toFloat
import com.suddenh4x.ratingdialog.utils.FeedbackUtils

@SuppressLint("InflateParams")
internal object DialogManager {
    private val TAG = DialogManager::class.java.simpleName
    private var rating: Float = -1f

    internal fun createRatingOverviewDialog(
        activity: FragmentActivity,
        dialogOptions: DialogOptions,
    ): AlertDialog {
        RatingLogger.debug(activity.getString(R.string.rating_dialog_log_rating_overview_creating_dialog))
        val builder = getDialogBuilder(activity, dialogOptions.customTheme)

        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ratingOverviewDialogBinding = DialogRatingOverviewBinding.inflate(inflater)
        initializeRatingDialogIcon(activity, ratingOverviewDialogBinding.imageView, dialogOptions)
        ratingOverviewDialogBinding.titleTextView.setText(dialogOptions.titleTextId)
        showOverviewMessage(dialogOptions, ratingOverviewDialogBinding.messageTextView)

        builder.apply {
            setView(ratingOverviewDialogBinding.root)

            setPositiveButton(dialogOptions.confirmButton.textId) { _, _ ->
                RatingLogger.debug(context.getString(R.string.rating_dialog_log_rating_overview_confirm_button_clicked))
                dialogOptions.confirmButton.confirmButtonClickListener?.onClick(rating)
                    ?: RatingLogger.info(context.getString(R.string.rating_dialog_log_rating_overview_confirm_button_no_click_listener))

                when {
                    rating >= dialogOptions.ratingThreshold.toFloat() -> {
                        RatingLogger.info(context.getString(R.string.rating_dialog_log_rating_overview_above_threshold))
                        showRatingDialog(dialogOptions, DialogType.RATING_STORE, activity)
                    }

                    dialogOptions.useCustomFeedback -> {
                        RatingLogger.info(context.getString(R.string.rating_dialog_log_rating_overview_below_threshold_with_custom_feedback))
                        PreferenceUtil.setDialogAgreed(context)
                        showRatingDialog(dialogOptions, DialogType.FEEDBACK_CUSTOM, activity)
                    }

                    else -> {
                        RatingLogger.info(context.getString(R.string.rating_dialog_log_rating_overview_below_threshold_without_custom_feedback))
                        PreferenceUtil.setDialogAgreed(context)
                        showRatingDialog(dialogOptions, DialogType.FEEDBACK_MAIL, activity)
                    }
                }
            }
            initializeRateLaterButton(activity, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(activity, dialogOptions, this)
        }

        return builder.create().also { dialog ->
            initRatingBar(ratingOverviewDialogBinding.ratingBar, dialogOptions.showOnlyFullStars, dialog)
        }
    }

    @SuppressLint("ResourceType")
    private fun showOverviewMessage(
        dialogOptions: DialogOptions,
        messageTextView: TextView,
    ) {
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
        activity: FragmentActivity,
    ) {
        RateDialogFragment.newInstance(dialogOptions, dialogType).show(activity.supportFragmentManager, TAG)
    }

    private fun initRatingBar(
        ratingBar: RatingBar,
        showOnlyFullStars: Boolean,
        dialog: AlertDialog,
    ) {
        ratingBar.apply {
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
        dialogOptions: DialogOptions,
    ): AlertDialog {
        RatingLogger.debug(context.getString(R.string.rating_dialog_log_rating_store_creating_dialog))
        val builder = getDialogBuilder(context, dialogOptions.customTheme)

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
                    RatingLogger.info(context.getString(R.string.rating_dialog_log_rating_store_rate_button_clicked))
                    PreferenceUtil.setDialogAgreed(context)

                    button.rateDialogClickListener?.onClick() ?: run {
                        RatingLogger.info(context.getString(R.string.rating_dialog_log_rating_store_default_click_listener))
                        FeedbackUtils.openPlayStoreListing(context)
                    }
                    dialogOptions.additionalRateNowButtonClickListener?.onClick()
                        ?: RatingLogger.info(context.getString(R.string.rating_dialog_log_rating_store_additional_click_listener_not_set))
                }
            }
            initializeRateLaterButton(context, dialogOptions.rateLaterButton, this)
            initializeRateNeverButton(context, dialogOptions, this)
        }
        return builder.create()
    }

    internal fun createMailFeedbackDialog(
        context: Context,
        dialogOptions: DialogOptions,
    ): AlertDialog {
        RatingLogger.debug(context.getString(R.string.rating_dialog_log_mail_feedback_creating_dialog))
        val builder = getDialogBuilder(context, dialogOptions.customTheme)

        builder.apply {
            setTitle(dialogOptions.feedbackTitleTextId)
            setMessage(dialogOptions.mailFeedbackMessageTextId)
            setCancelable(dialogOptions.cancelable)

            dialogOptions.mailFeedbackButton.let { button ->
                setPositiveButton(button.textId) { _, _ ->
                    RatingLogger.info(context.getString(R.string.rating_dialog_log_mail_feedback_button_clicked))

                    button.rateDialogClickListener?.onClick()
                        ?: openMailFeedback(context, dialogOptions.mailSettings)

                    dialogOptions.additionalMailFeedbackButtonClickListener?.onClick()
                        ?: RatingLogger.info(context.getString(R.string.rating_dialog_log_mail_feedback_additional_click_listener_not_set))
                }
            }
            initializeNoFeedbackButton(context, dialogOptions.noFeedbackButton, this)
        }
        return builder.create()
    }

    private fun openMailFeedback(
        context: Context,
        mailSettings: MailSettings?,
    ) {
        if (mailSettings != null) {
            FeedbackUtils.openMailFeedback(context, mailSettings)
        } else {
            RatingLogger.error(context.getString(R.string.rating_dialog_log_mail_feedback_no_mail_settings))
        }
    }

    internal fun createCustomFeedbackDialog(
        context: Context,
        dialogOptions: DialogOptions,
    ): AlertDialog {
        RatingLogger.debug(context.getString(R.string.rating_dialog_log_custom_feedback_creating_dialog))
        val builder = getDialogBuilder(context, dialogOptions.customTheme)

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
                    RatingLogger.info(context.getString(R.string.rating_dialog_log_custom_feedback_button_clicked))

                    val userFeedbackText = customFeedbackEditText.text.toString()
                    button.customFeedbackButtonClickListener?.onClick(userFeedbackText)
                        ?: RatingLogger.error(context.getString(R.string.rating_dialog_log_custom_feedback_no_click_listener))
                }
            }
            initializeNoFeedbackButton(context, dialogOptions.noFeedbackButton, this)
        }
        return builder.create().also { dialog ->
            initializeCustomFeedbackDialogButtonHandler(customFeedbackEditText, dialog)
        }
    }

    private fun initializeCustomFeedbackDialogButtonHandler(
        editText: EditText,
        dialog: AlertDialog,
    ) {
        editText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = (count > 0)
                }
            },
        )
    }

    private fun disablePositiveButtonWhenDialogShows(dialog: AlertDialog) {
        dialog.setOnShowListener { visibleDialog ->
            (visibleDialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
    }

    private fun initializeRatingDialogIcon(
        context: Context,
        imageView: ImageView,
        dialogOptions: DialogOptions,
    ) {
        if (dialogOptions.iconDrawable != null) {
            RatingLogger.info(context.getString(R.string.rating_dialog_log_use_custom_rating_dialog_icon))
            imageView.setImageDrawable(dialogOptions.iconDrawable)
        } else {
            RatingLogger.info(context.getString(R.string.rating_dialog_log_use_app_icon))
            val appIcon = context.packageManager.getApplicationIcon(context.applicationInfo)
            imageView.setImageDrawable(appIcon)
        }
    }

    private fun initializeRateLaterButton(
        context: Context,
        rateLaterButton: RateButton,
        dialogBuilder: AlertDialog.Builder,
    ) {
        dialogBuilder.setNeutralButton(rateLaterButton.textId) { _, _ ->
            RatingLogger.info(context.getString(R.string.rating_dialog_log_rate_later_button_clicked))
            PreferenceUtil.onLaterButtonClicked(context)
            rateLaterButton.rateDialogClickListener?.onClick()
                ?: RatingLogger.info(context.getString(R.string.rating_dialog_log_rate_later_button_no_click_listener))
        }
    }

    private fun initializeRateNeverButton(
        context: Context,
        dialogOptions: DialogOptions,
        dialogBuilder: AlertDialog.Builder,
    ) {
        val countOfLaterButtonClicksToShowNeverButton = dialogOptions.countOfLaterButtonClicksToShowNeverButton
        val numberOfLaterButtonClicks = PreferenceUtil.getNumberOfLaterButtonClicks(context)
        RatingLogger.debug(context.getString(R.string.rating_dialog_log_rate_later_button_was_clicked, numberOfLaterButtonClicks))
        if (countOfLaterButtonClicksToShowNeverButton > numberOfLaterButtonClicks) {
            RatingLogger.info(
                context.getString(R.string.rating_dialog_log_rate_later_button_dont_show_never, countOfLaterButtonClicksToShowNeverButton),
            )
            return
        }

        dialogOptions.rateNeverButton?.let { button ->
            dialogBuilder.setNegativeButton(button.textId) { _, _ ->
                RatingLogger.info(context.getString(R.string.rating_dialog_log_rate_never_button_clicked))
                PreferenceUtil.setDoNotShowAgain(context)
                button.rateDialogClickListener?.onClick()
                    ?: RatingLogger.info(context.getString(R.string.rating_dialog_log_rate_never_button_no_click_listener))
            }
        }
    }

    private fun initializeNoFeedbackButton(
        context: Context,
        noFeedbackButton: RateButton,
        dialogBuilder: AlertDialog.Builder,
    ) {
        noFeedbackButton.let { button ->
            dialogBuilder.setNegativeButton(button.textId) { _, _ ->
                RatingLogger.info(context.getString(R.string.rating_dialog_log_no_feedback_button_clicked))
                button.rateDialogClickListener?.onClick()
                    ?: RatingLogger.info(context.getString(R.string.rating_dialog_log_no_feedback_button_no_click_listener))
            }
        }
    }

    private fun getDialogBuilder(
        context: Context,
        theme: Int,
    ): AlertDialog.Builder {
        return try {
            MaterialAlertDialogBuilder(context, theme)
        } catch (ex: IllegalArgumentException) {
            RatingLogger.debug(context.getString(R.string.rating_dialog_log_no_material_components_theme_used))
            AlertDialog.Builder(context, theme)
        }
    }
}
