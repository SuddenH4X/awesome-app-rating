package com.suddenh4x.ratingdialog.buttons

import androidx.annotation.StringRes

internal class RateButton(@StringRes var textId: Int, var rateDialogClickListener: RateDialogClickListener?)

internal class ConfirmButton(
    @StringRes var textId: Int,
    var confirmButtonClickListener: ConfirmButtonClickListener?
)

internal class CustomFeedbackButton(
    @StringRes var textId: Int,
    var customFeedbackButtonClickListener: CustomFeedbackButtonClickListener?
)
