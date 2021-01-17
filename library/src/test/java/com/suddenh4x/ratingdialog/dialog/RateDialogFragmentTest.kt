package com.suddenh4x.ratingdialog.dialog

import android.os.Bundle
import com.suddenh4x.ratingdialog.logging.RatingLogger
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
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

        rateDialogFragmentSpy = spyk(RateDialogFragment())
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

    /* fixme: getProperty on spyk object is currently not working: https://github.com/mockk/mockk/issues/263
    @Test
    fun `on start with argument FEEDBACK_CUSTOM disables button`() {
        val dialog = mockk<AlertDialog>(relaxed = true)
        every { rateDialogFragment getProperty ("dialogType") } returns DialogType.FEEDBACK_CUSTOM
        every { rateDialogFragment.dialog } returns dialog
        rateDialogFragment.onStart()
        verify(exactly = 1) { dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
    }*/
}
