package com.suddenh4x.ratingdialog.buttons

interface RateDialogClickListener {
    fun onClick()
}

interface ConfirmButtonClickListener {
    fun onClick(userRating: Float)
}

interface CustomFeedbackButtonClickListener {
    fun onClick(userFeedbackText: String)
}
