package com.suddenh4x.ratingdialog.buttons

import java.io.Serializable

interface RateDialogClickListener : Serializable {
    fun onClick()
}

interface ConfirmButtonClickListener : Serializable {
    fun onClick(userRating: Float)
}

interface CustomFeedbackButtonClickListener : Serializable {
    fun onClick(userFeedbackText: String)
}
