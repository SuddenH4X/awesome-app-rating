package com.suddenh4x.ratingdialog.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil

internal class RateDialogFragment : DialogFragment() {

    // cannot use by lazy because of mocking limitations of mockk
    @VisibleForTesting
    internal lateinit var dialogType: DialogType

    // cannot use by lazy because of mocking limitations of mockk
    @VisibleForTesting
    internal lateinit var dialogOptions: DialogOptions

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        dialogOptions = arguments?.getSerializable(ARG_DIALOG_OPTIONS) as DialogOptions
        dialogType = arguments?.getSerializable(ARG_DIALOG_TYPE) as DialogType? ?: DialogType.RATING_OVERVIEW
        isCancelable = dialogOptions.cancelable

        return when (dialogType) {
            DialogType.RATING_OVERVIEW -> DialogManager.createRatingOverviewDialog(
                requireActivity(),
                dialogOptions,
            )

            DialogType.RATING_STORE -> DialogManager.createRatingStoreDialog(
                requireActivity(),
                dialogOptions,
            )

            DialogType.FEEDBACK_MAIL -> DialogManager.createMailFeedbackDialog(
                requireActivity(),
                dialogOptions,
            )

            DialogType.FEEDBACK_CUSTOM -> DialogManager.createCustomFeedbackDialog(
                requireActivity(),
                dialogOptions,
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialogType == DialogType.FEEDBACK_CUSTOM) {
            (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        RatingLogger.info("Dialog was canceled.")
        PreferenceUtil.onLaterButtonClicked(requireContext())
        dialogOptions.dialogCancelListener?.invoke()
            ?: RatingLogger.info("Dialog cancel listener isn't set.")
    }

    companion object {
        internal const val ARG_DIALOG_TYPE = "DialogType"
        internal const val ARG_DIALOG_OPTIONS = "DialogOptions"

        fun newInstance(dialogOptions: DialogOptions): DialogFragment {
            val rateDialogFragment = RateDialogFragment()
            rateDialogFragment.arguments = Bundle().apply {
                putSerializable(ARG_DIALOG_OPTIONS, dialogOptions)
            }
            return rateDialogFragment
        }

        fun newInstance(dialogOptions: DialogOptions, dialogType: DialogType): DialogFragment {
            val rateDialogFragment = RateDialogFragment()
            rateDialogFragment.arguments = Bundle().apply {
                putSerializable(ARG_DIALOG_OPTIONS, dialogOptions)
                putSerializable(ARG_DIALOG_TYPE, dialogType)
            }
            return rateDialogFragment
        }
    }
}
