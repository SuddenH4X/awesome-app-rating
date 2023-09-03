package com.suddenh4x.ratingdialog.exampleapp.composeexample

sealed interface ComposeExampleUiState {

    data object NoSnackbar : ComposeExampleUiState

    data class Snackbar(val snackbarText: String) : ComposeExampleUiState
}
