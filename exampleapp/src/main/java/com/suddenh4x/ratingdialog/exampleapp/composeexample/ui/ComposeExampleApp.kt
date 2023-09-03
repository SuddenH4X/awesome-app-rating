package com.suddenh4x.ratingdialog.exampleapp.composeexample.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.suddenh4x.ratingdialog.exampleapp.R
import com.suddenh4x.ratingdialog.exampleapp.composeexample.ComposeExampleUiState
import com.suddenh4x.ratingdialog.exampleapp.composeexample.composetheme.DarkColorScheme
import com.suddenh4x.ratingdialog.exampleapp.composeexample.composetheme.LightColorScheme

@Composable
fun ComposeExampleApp(
    uiState: ComposeExampleUiState,
    openGoogleInAppReview: () -> Unit,
    dismissSnackbar: () -> Unit,
    finishActivity: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val colorScheme = if (!isSystemInDarkTheme()) LightColorScheme else DarkColorScheme

    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { ComposeExampleTopAppBar(finishActivity = finishActivity) },
        ) { paddingValues ->
            when (uiState) {
                is ComposeExampleUiState.Snackbar -> LaunchedEffect(key1 = uiState) {
                    snackbarHostState.showSnackbar(message = uiState.snackbarText)
                    dismissSnackbar()
                }

                is ComposeExampleUiState.NoSnackbar -> Unit
            }

            ComposeExample(
                paddingValues = paddingValues,
                openGoogleInAppReview = openGoogleInAppReview,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeExampleTopAppBar(finishActivity: () -> Unit) {
    TopAppBar(
        title = { Text("Jetpack Compose Example") },
        navigationIcon = { NavigateBackIcon(finishActivity) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}

@Composable
private fun NavigateBackIcon(finishActivity: () -> Unit) {
    IconButton(onClick = { finishActivity() }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Navigate back",
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
fun ComposeExample(
    paddingValues: PaddingValues,
    openGoogleInAppReview: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize(),
    ) {
        Text(
            text = stringResource(R.string.text_example_jetpack_compose),
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            onClick = { openGoogleInAppReview() },
        ) {
            Text(text = stringResource(R.string.button_example_jetpack_compose))
        }
    }
}

@Preview
@Composable
fun ComposeExampleAppPreview() {
    ComposeExampleApp(
        uiState = ComposeExampleUiState.NoSnackbar,
        openGoogleInAppReview = {},
        dismissSnackbar = {},
        finishActivity = {}
    )
}

@Preview
@Composable
fun JetpackComposeExampleTopAppBarPreview() {
    ComposeExampleTopAppBar {}
}

@Preview
@Composable
fun JetpackComposeExamplePreview() {
    ComposeExample(paddingValues = PaddingValues(), openGoogleInAppReview = {})
}
