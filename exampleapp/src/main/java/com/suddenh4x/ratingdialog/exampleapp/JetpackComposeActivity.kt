package com.suddenh4x.ratingdialog.exampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme
import com.suddenh4x.ratingdialog.AppRating
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class JetpackComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jetpack_compose)
        AppRating.reset(this)

        val composeView = findViewById<ComposeView>(R.id.ComposeView)
        composeView.setContent {
            val snackbarHostState = remember { SnackbarHostState() }

            MdcTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = { JetpackComposeExampleTopAppBar() },
                ) { paddingValues ->
                    JetpackComposeExample(paddingValues, snackbarHostState)
                }
            }
        }
    }

    @Composable
    private fun JetpackComposeExampleTopAppBar() {
        TopAppBar(
            title = { Text("Jetpack Compose Example") },
        )
    }

    @Composable
    fun JetpackComposeExample(paddingValues: PaddingValues, snackbarHostState: SnackbarHostState) {
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Text(
                text = "This is a Jetpack Compose example with a ComponentActivity. So the button below only " +
                    "starts the Google in-app review. If you want to use the libraries dialog with Jetpack Compose " +
                    "your activity has to extend from FragmentActivity (e.g. ComponentActivity).",
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                onClick = { openGoogleInAppReview(coroutineScope, snackbarHostState) },
            ) {
                Text(text = "Jetpack Compose Example")
            }
        }
    }

    private fun openGoogleInAppReview(
        coroutineScope: CoroutineScope,
        snackbarHostState: SnackbarHostState
    ) {
        AppRating.Builder(this@JetpackComposeActivity)
            .useGoogleInAppReview()
            .setGoogleInAppReviewCompleteListener { successful ->
                showSnackbar(coroutineScope, snackbarHostState, successful)
            }
            .setDebug(true)
            .showIfMeetsConditions()
    }

    private fun showSnackbar(
        coroutineScope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        successful: Boolean
    ) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                "Google in-app review completed " +
                    "(successful: $successful)",
            )
        }
    }

    @Preview
    @Composable
    fun JetpackComposeExamplePreview() {
        val snackbarHostState = remember { SnackbarHostState() }
        JetpackComposeExample(PaddingValues(), snackbarHostState)
    }
}
