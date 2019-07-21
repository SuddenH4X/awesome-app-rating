package com.suddenh4x.awesome_app_rate.buttons

interface RateDialogClickListener {
    fun onClick()
}

interface CustomFeedbackButtonClickListener {
    fun onClick(userFeedbackText: String)
}
