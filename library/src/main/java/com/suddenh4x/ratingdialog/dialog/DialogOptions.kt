package com.suddenh4x.ratingdialog.dialog

import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import com.suddenh4x.ratingdialog.R
import com.suddenh4x.ratingdialog.buttons.CustomFeedbackButton
import com.suddenh4x.ratingdialog.buttons.RateButton
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.RatingThreshold

internal object DialogOptions {
    var iconDrawable: Drawable? = null
    var rateLaterButton: RateButton = RateButton(R.string.rating_dialog_button_rate_later, null)
    var rateNeverButton: RateButton? = null
    var ratingThreshold: RatingThreshold = RatingThreshold.THREE

    // rating dialog overview
    @StringRes
    var titleTextId = R.string.rating_dialog_overview_title
    @StringRes
    var messageTextId: Int? = null
    @StringRes
    var confirmButtonTextId = R.string.rating_dialog_overview_button_confirm

    // rating dialog store
    @StringRes
    var storeRatingTitleTextId = R.string.rating_dialog_store_title
    @StringRes
    var storeRatingMessageTextId = R.string.rating_dialog_store_message
    lateinit var rateNowButton: RateButton

    // rating dialog feedback
    @StringRes
    var feedbackTitleTextId = R.string.rating_dialog_feedback_title
    var noFeedbackButton: RateButton = RateButton(R.string.rating_dialog_feedback_button_no, null)

    // rating dialog mail feedback
    @StringRes
    var mailFeedbackMessageTextId = R.string.rating_dialog_feedback_mail_message
    var mailFeedbackButton: RateButton = RateButton(R.string.rating_dialog_feedback_mail_button_mail, null)
    var mailSettings: MailSettings? = null

    // rating dialog custom feedback
    var useCustomFeedback = false
    @StringRes
    var customFeedbackMessageTextId = R.string.rating_dialog_feedback_custom_message
    var customFeedbackButton: CustomFeedbackButton =
        CustomFeedbackButton(R.string.rating_dialog_feedback_custom_button_submit, null)

    // other settings
    var cancelable = false
}
