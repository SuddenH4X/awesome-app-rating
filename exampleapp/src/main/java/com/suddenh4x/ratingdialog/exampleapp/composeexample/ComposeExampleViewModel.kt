package com.suddenh4x.ratingdialog.exampleapp.composeexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ComposeExampleViewModel : ViewModel() {

    private val viewModelState = MutableStateFlow(ComposeExampleViewModelState())

    val uiState = viewModelState.map(ComposeExampleViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    fun showSnackbar(successful: Boolean) =
        viewModelState.update {
            it.copy(snackbarText = "Google in-app review completed (successful: $successful)")
        }

    fun dismissSnackbar() = viewModelState.update { it.copy(snackbarText = null) }
}
