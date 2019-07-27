package com.suddenh4x.ratingdialog.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

internal class RateDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialogOptions = arguments?.getSerializable(ARG_DIALOG_OPTIONS) as DialogOptions
        isCancelable = dialogOptions.cancelable
        return DialogManager.createRatingOverviewDialog(requireContext(), dialogOptions)
    }

    companion object {
        const val ARG_DIALOG_OPTIONS = "DialogOptions"
    }
}
