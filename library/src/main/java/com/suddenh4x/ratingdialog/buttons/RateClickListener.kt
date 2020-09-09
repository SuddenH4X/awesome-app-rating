package com.suddenh4x.ratingdialog.buttons

fun interface RateDialogClickListener {
    fun onClick()
}

fun interface ConfirmButtonClickListener {
    fun onClick(userRating: Float)
}

fun interface CustomFeedbackButtonClickListener {
    fun onClick(userFeedbackText: String)
}
