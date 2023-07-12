package com.suddenh4x.ratingdialog.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.suddenh4x.ratingdialog.enums.AppStore
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.MailSettings
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class FeedbackUtilsTest {
    @MockK
    lateinit var context: Context

    @MockK
    lateinit var uri: Uri

    @BeforeEach
    fun setup() {
        RatingLogger.isLoggingEnabled = false
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns uri
    }

    @Nested
    inner class OpenPlayStoreListing {

        @BeforeEach
        fun setup() {
            mockkConstructor(Intent::class)
            every { context.packageName } returns PACKAGE_NAME
        }

        @Test
        fun `opens correct in app url`() {
            every { context.startActivity(any()) } just Runs

            FeedbackUtils.openStoreListing(context, store = AppStore.GOOGLE_PLAYSTORE)
            verify(exactly = 1) { Uri.parse(any()) }
            verify(exactly = 1) { Uri.parse(FeedbackUtils.GOOGLE_PLAY_IN_APP_URL + PACKAGE_NAME) }
            verify(exactly = 1) { context.startActivity(any()) }
        }

        @Test
        fun `opens correct web url if Play Store hasn't been found`() {
            every { context.startActivity(any()) } throws ActivityNotFoundException() andThen {}

            FeedbackUtils.openStoreListing(context, AppStore.GOOGLE_PLAYSTORE)
            verify(exactly = 2) { Uri.parse(any()) }
            verify(exactly = 1) { Uri.parse(FeedbackUtils.GOOGLE_PLAY_IN_APP_URL + PACKAGE_NAME) }
            verify(exactly = 1) { Uri.parse(FeedbackUtils.GOOGLE_PLAY_WEB_URL + PACKAGE_NAME) }
            verify(exactly = 2) { context.startActivity(any()) }
        }
    }

    @Nested
    inner class OpenMailFeedback {
        @MockK
        lateinit var intent: Intent

        @BeforeEach
        fun setup() {
            mockkConstructor(Intent::class)
            every { anyConstructed<Intent>().setAction(any()) } returns intent
            every { anyConstructed<Intent>().setData(any()) } returns intent
            every { anyConstructed<Intent>().putExtra(any(), any<String>()) } returns intent
            every { anyConstructed<Intent>().putExtra(any(), any<Array<String>>()) } returns intent
            every { context.packageManager } returns mockk()
        }

        @Test
        fun `opens correct mail intent`() {
            every { anyConstructed<Intent>().resolveActivity(any()) } returns mockk()
            every { context.startActivity(any()) } just Runs
            FeedbackUtils.openMailFeedback(context, mailSettings)

            verify(exactly = 1) { Uri.parse(any()) }
            verify(exactly = 1) { Uri.parse(FeedbackUtils.URI_SCHEME_MAIL_TO) }
            verify(exactly = 1) { anyConstructed<Intent>().data = uri }
            verify(exactly = 1) {
                anyConstructed<Intent>()
                    .putExtra(Intent.EXTRA_EMAIL, arrayOf(mailSettings.mailAddress))
            }
            verify(exactly = 1) {
                anyConstructed<Intent>().putExtra(Intent.EXTRA_SUBJECT, mailSettings.subject)
            }
            verify(exactly = 1) {
                anyConstructed<Intent>().putExtra(Intent.EXTRA_TEXT, mailSettings.text)
            }
            verify(exactly = 1) { context.startActivity(any()) }
        }

        @Test
        fun `shows error toast if no mail app was found`() {
            mockkStatic(Toast::class)
            every { anyConstructed<Intent>().resolveActivity(any()) } returns null
            every { Toast.makeText(context, any<String>(), Toast.LENGTH_LONG) } returns mockk(
                relaxed = true,
            )
            FeedbackUtils.openMailFeedback(context, mailSettings)

            verify(exactly = 0) { context.startActivity(any()) }
            verify(exactly = 1) {
                Toast.makeText(
                    context,
                    mailSettings.errorToastMessage,
                    Toast.LENGTH_LONG,
                )
            }
        }
    }

    companion object {
        private const val PACKAGE_NAME = "com.suddenh4x.unittest"
        private val mailSettings = MailSettings("address", "subject", "message", "errorToast")
    }
}
