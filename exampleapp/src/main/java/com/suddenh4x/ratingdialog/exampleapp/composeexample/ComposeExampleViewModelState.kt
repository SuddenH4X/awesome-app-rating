package com.suddenh4x.ratingdialog.exampleapp.composeexample

data class ComposeExampleViewModelState(val snackbarText: String? = null) {

    fun toUiState(): ComposeExampleUiState =
        when {
            snackbarText != null -> ComposeExampleUiState.Snackbar(snackbarText)
            else -> ComposeExampleUiState.NoSnackbar
        }
}
