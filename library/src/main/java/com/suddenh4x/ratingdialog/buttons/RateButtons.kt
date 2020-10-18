package com.suddenh4x.ratingdialog.buttons

import androidx.annotation.StringRes
import java.io.Serializable

internal class RateButton(
    @StringRes var textId: Int,
    var rateDialogClickListener: RateDialogClickListener?
) : Serializable

internal class ConfirmButton(
    @StringRes var textId: Int,
    var confirmButtonClickListener: ConfirmButtonClickListener?
) : Serializable

internal class CustomFeedbackButton(
    @StringRes var textId: Int,
    var customFeedbackButtonClickListener: CustomFeedbackButtonClickListener?
) : Serializable
