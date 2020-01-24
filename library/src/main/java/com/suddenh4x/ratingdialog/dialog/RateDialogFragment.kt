package com.suddenh4x.ratingdialog.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

internal class RateDialogFragment : DialogFragment() {
    private lateinit var dialogType: DialogType

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialogOptions: DialogOptions =
            arguments?.getSerializable(ARG_DIALOG_OPTIONS) as DialogOptions
        isCancelable = dialogOptions.cancelable

        dialogType =
            arguments?.getSerializable(ARG_DIALOG_TYPE) as DialogType? ?: DialogType.RATING_OVERVIEW
        return when (dialogType) {
            DialogType.RATING_OVERVIEW -> DialogManager.createRatingOverviewDialog(
                requireActivity(),
                dialogOptions
            )
            DialogType.RATING_STORE -> DialogManager.createRatingStoreDialog(
                requireActivity(),
                dialogOptions
            )
            DialogType.FEEDBACK_MAIL -> DialogManager.createMailFeedbackDialog(
                requireActivity(),
                dialogOptions
            )
            DialogType.FEEDBACK_CUSTOM -> DialogManager.createCustomFeedbackDialog(
                requireActivity(),
                dialogOptions
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialogType == DialogType.FEEDBACK_CUSTOM) {
            (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
    }

    companion object {
        private const val ARG_DIALOG_TYPE = "DialogType"
        private const val ARG_DIALOG_OPTIONS = "DialogOptions"

        fun newInstance(dialogOptions: DialogOptions): RateDialogFragment {
            val rateDialogFragment = RateDialogFragment()
            rateDialogFragment.arguments = Bundle().apply {
                putSerializable(ARG_DIALOG_OPTIONS, dialogOptions)
            }
            return rateDialogFragment
        }

        fun newInstance(dialogOptions: DialogOptions, dialogType: DialogType): RateDialogFragment {
            val rateDialogFragment = RateDialogFragment()
            rateDialogFragment.arguments = Bundle().apply {
                putSerializable(ARG_DIALOG_OPTIONS, dialogOptions)
                putSerializable(ARG_DIALOG_TYPE, dialogType)
            }
            return rateDialogFragment
        }
    }
}
