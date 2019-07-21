package com.suddenh4x.awesome_app_rate.buttons

import androidx.annotation.StringRes

internal class RateButton(@StringRes val textId: Int, val rateDialogClickListener: RateDialogClickListener?)

internal class CustomFeedbackButton(@StringRes val textId: Int, val customFeedbackButtonClickListener: CustomFeedbackButtonClickListener?)
