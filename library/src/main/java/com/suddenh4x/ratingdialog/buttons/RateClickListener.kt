package com.suddenh4x.ratingdialog.buttons

interface RateDialogClickListener {
    fun onClick()
}

interface CustomFeedbackButtonClickListener {
    fun onClick(userFeedbackText: String)
}
