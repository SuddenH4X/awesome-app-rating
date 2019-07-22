package com.suddenh4x.ratingdialog.buttons

import androidx.annotation.StringRes

internal class RateButton(@StringRes val textId: Int, val rateDialogClickListener: RateDialogClickListener?)

internal class CustomFeedbackButton(@StringRes val textId: Int, val customFeedbackButtonClickListener: CustomFeedbackButtonClickListener?)
