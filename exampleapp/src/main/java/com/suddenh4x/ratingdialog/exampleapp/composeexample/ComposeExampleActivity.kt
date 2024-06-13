package com.suddenh4x.ratingdialog.exampleapp.composeexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.exampleapp.R
import com.suddenh4x.ratingdialog.exampleapp.composeexample.ui.ComposeExampleApp

class ComposeExampleActivity : ComponentActivity() {
    private val viewModel: ComposeExampleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jetpack_compose)
        AppRating.reset(this)

        val composeView = findViewById<ComposeView>(R.id.ComposeView)
        composeView.setContent {
            val uiState by viewModel.uiState.collectAsState()

            ComposeExampleApp(
                uiState = uiState,
                openGoogleInAppReview = ::openGoogleInAppReview,
                dismissSnackbar = viewModel::dismissSnackbar,
                finishActivity = ::finish,
            )
        }
    }

    private fun openGoogleInAppReview() {
        AppRating.Builder(this)
            .useGoogleInAppReview()
            .setGoogleInAppReviewCompleteListener { successful ->
                viewModel.showSnackbar(successful)
            }
            .setDebug(true)
            .showIfMeetsConditions()
    }
}
