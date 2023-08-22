package com.suddenh4x.ratingdialog.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.suddenh4x.ratingdialog.R

class BottomSheetAlertDialog : BottomSheetDialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, theme: Int) : super(context, theme)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(
        context,
        cancelable,
        cancelListener
    )

    override fun setContentView(view: View) {
        super.setContentView(wrapInAlert(view))
    }

    private fun wrapInAlert(view: View): View {
        val alert = LayoutInflater.from(context).inflate(R.layout.sheet_alert_dialog, null)
        alert.findViewById<FrameLayout>(R.id.content).addView(
            view,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        return alert
    }

    internal fun getButton(whichButton: Int): Button? {
        when (whichButton) {
            AlertDialog.BUTTON_POSITIVE -> return findViewById<MaterialButton>(R.id.positive)
            AlertDialog.BUTTON_NEGATIVE -> return findViewById<MaterialButton>(R.id.negative)
            AlertDialog.BUTTON_NEUTRAL -> return findViewById<MaterialButton>(R.id.neutral)
        }
        throw IllegalArgumentException("Button must be one of BUTTON_POSITIVE, BUTTON_NEGATIVE, BUTTON_NEUTRAL")
    }


    internal fun setButton(whichButton: Int, text: CharSequence?, listener: DialogInterface.OnClickListener?) {
        getButton(whichButton)?.let {
            it.text = text
            it.visibility = View.VISIBLE
            it.setOnClickListener { listener?.onClick(this@BottomSheetAlertDialog, whichButton) }
        }
    }
}
