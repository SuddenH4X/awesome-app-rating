package com.suddenh4x.ratingdialog.preferences

import java.io.Serializable

data class MailSettings(
    val mailAddress: String,
    val subject: String,
    val text: String? = null,
    val errorToastMessage: String? = null,
) : Serializable
