package com.suddenh4x.ratingdialog.dialog

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.suddenh4x.ratingdialog.logging.RatingLogger
import com.suddenh4x.ratingdialog.preferences.PreferenceUtil
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
class RateDialogFragmentTest {

    @MockK
    lateinit var bundle: Bundle

    @MockK
    internal lateinit var dialogOptions: DialogOptions

    private lateinit var rateDialogFragmentSpy: RateDialogFragment

    @BeforeEach
    fun setup() {
        RatingLogger.isLoggingEnabled = false
        mockkObject(DialogManager)
        mockkStatic(Log::class)

        rateDialogFragmentSpy = spyk(RateDialogFragment())
        every { Log.isLoggable(any(), any()) } returns false
        every { bundle.getSerializable(RateDialogFragment.ARG_DIALOG_TYPE) } returns null
        every { bundle.getSerializable(RateDialogFragment.ARG_DIALOG_OPTIONS) } returns dialogOptions
        every { rateDialogFragmentSpy.arguments } returns bundle
        every { rateDialogFragmentSpy.requireActivity() } returns mockk()
        every { rateDialogFragmentSpy.requireContext() } returns mockk()
        every { dialogOptions.cancelable } returns false
        every { DialogManager.createRatingOverviewDialog(any(), any()) } returns mockk()
    }

    @AfterEach
    fun shutdown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `onCreateDialog without arguments shows rating overview dialog`() {
        rateDialogFragmentSpy.onCreateDialog(mockk())

        verify(exactly = 1) { DialogManager.createRatingOverviewDialog(any(), any()) }
    }

    @Test
    fun `onCreateDialog with argument RATING_OVERVIEW shows rating overview dialog`() {
        every { bundle.getSerializable(RateDialogFragment.ARG_DIALOG_TYPE) } returns DialogType.RATING_OVERVIEW
        every { DialogManager.createRatingOverviewDialog(any(), any()) } returns mockk()

        rateDialogFragmentSpy.onCreateDialog(mockk())

        verify(exactly = 1) { DialogManager.createRatingOverviewDialog(any(), any()) }
    }

    @Test
    fun `onCreateDialog with argument RATING_STORE shows rating store dialog`() {
        every { bundle.getSerializable(RateDialogFragment.ARG_DIALOG_TYPE) } returns DialogType.RATING_STORE
        every { DialogManager.createRatingStoreDialog(any(), any()) } returns mockk()

        rateDialogFragmentSpy.onCreateDialog(mockk())

        verify(exactly = 1) { DialogManager.createRatingStoreDialog(any(), any()) }
    }

    @Test
    fun `onCreateDialog with argument FEEDBACK_MAIL shows mail feedback dialog`() {
        every { bundle.getSerializable(RateDialogFragment.ARG_DIALOG_TYPE) } returns DialogType.FEEDBACK_MAIL
        every { DialogManager.createMailFeedbackDialog(any(), any()) } returns mockk()

        rateDialogFragmentSpy.onCreateDialog(mockk())

        verify(exactly = 1) { DialogManager.createMailFeedbackDialog(any(), any()) }
    }

    @Test
    fun `onCreateDialog with argument FEEDBACK_CUSTOM shows custom feedback dialog`() {
        every { bundle.getSerializable(RateDialogFragment.ARG_DIALOG_TYPE) } returns DialogType.FEEDBACK_CUSTOM
        every { DialogManager.createCustomFeedbackDialog(any(), any()) } returns mockk()

        rateDialogFragmentSpy.onCreateDialog(mockk())

        verify(exactly = 1) { DialogManager.createCustomFeedbackDialog(any(), any()) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `onCreateDialog sets isCancelable to the value of dialogOptions`(isCancelable: Boolean) {
        every { dialogOptions.cancelable } returns isCancelable

        rateDialogFragmentSpy.onCreateDialog(mockk())

        verify(exactly = 1) { rateDialogFragmentSpy.isCancelable = isCancelable }
    }

    @Nested
    inner class OnCancel {

        @BeforeEach
        fun setup() {
            mockkObject(PreferenceUtil)
            rateDialogFragmentSpy.dialogOptions = dialogOptions
            every { PreferenceUtil.onLaterButtonClicked(any()) } just Runs
            every { dialogOptions.dialogCancelListener } returns null
        }

        @Test
        fun `calls PreferenceUtils_onLaterButtonClicked`() {
            rateDialogFragmentSpy.onCancel(mockk())

            verify(exactly = 1) { PreferenceUtil.onLaterButtonClicked(any()) }
        }

        @Test
        fun `invokes dialogCancelListener`() {
            val dialogCancelListener: () -> Unit = mockk()
            every { dialogCancelListener() } just Runs
            every { dialogOptions.dialogCancelListener } returns dialogCancelListener

            rateDialogFragmentSpy.onCancel(mockk())

            verify(exactly = 1) { dialogCancelListener.invoke() }
        }
    }

    @Nested
    inner class OnStart {

        @MockK
        lateinit var dialog: AlertDialog

        @BeforeEach
        fun setup() {
            every { rateDialogFragmentSpy.dialog } returns dialog
        }

        @Test
        fun `with argument FEEDBACK_CUSTOM disables positive button`() {
            every { dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false } just Runs
            rateDialogFragmentSpy.dialogType = DialogType.FEEDBACK_CUSTOM

            rateDialogFragmentSpy.onStart()

            verify(exactly = 1) { dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
        }

        @ParameterizedTest
        @EnumSource(
            value = DialogType::class,
            names = ["FEEDBACK_MAIL", "RATING_OVERVIEW", "RATING_STORE"],
        )
        fun `with other arguments doesn't disable positive button`(dialogType: DialogType) {
            rateDialogFragmentSpy.dialogType = dialogType

            rateDialogFragmentSpy.onStart()

            verify(exactly = 0) { dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
        }
    }
}
