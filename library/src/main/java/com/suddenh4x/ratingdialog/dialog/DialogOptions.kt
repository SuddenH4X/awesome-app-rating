package com.suddenh4x.ratingdialog.dialog

import com.suddenh4x.ratingdialog.preferences.MailSettings
import java.io.Serializable

internal class DialogOptions : Serializable {

    var customCondition: (() -> Boolean)? = null
    var customConditionToShowAgain: (() -> Boolean)? = null
    var countAppLaunch: Boolean = true

    /**
     * rating dialog mail feedback
     */

    var mailSettings: MailSettings? = null

    /**
     * Google in-app review
     */

    var googleInAppReviewCompleteListener: ((Boolean) -> Unit)? = null
}
