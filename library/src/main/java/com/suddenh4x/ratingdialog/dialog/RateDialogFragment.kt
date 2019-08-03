package com.suddenh4x.ratingdialog.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

internal class RateDialogFragment : DialogFragment() {
    private lateinit var dialogType: DialogType

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        isCancelable = DialogOptions.cancelable

        dialogType = arguments?.getSerializable(ARG_DIALOG_TYPE) as DialogType? ?: DialogType.RATING_OVERVIEW
        return when (dialogType) {
            DialogType.RATING_OVERVIEW -> DialogManager.createRatingOverviewDialog(requireActivity(), DialogOptions)
            DialogType.RATING_STORE -> DialogManager.createRatingStoreDialog(requireActivity(), DialogOptions)
            DialogType.FEEDBACK_MAIL -> DialogManager.createMailFeedbackDialog(requireActivity(), DialogOptions)
            DialogType.FEEDBACK_CUSTOM -> DialogManager.createCustomFeedbackDialog(requireActivity(), DialogOptions)
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialogType == DialogType.FEEDBACK_CUSTOM) {
            (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
    }

    companion object {
        const val ARG_DIALOG_TYPE = "DialogOptions"
    }
}
