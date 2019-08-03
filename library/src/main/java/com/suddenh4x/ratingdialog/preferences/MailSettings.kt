package com.suddenh4x.ratingdialog.preferences

data class MailSettings(
    val mailAddress: String,
    val subject: String,
    val text: String? = null,
    val chooserTitle: String? = null
)
