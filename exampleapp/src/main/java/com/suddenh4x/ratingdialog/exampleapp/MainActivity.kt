package com.suddenh4x.ratingdialog.exampleapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.buttons.CustomFeedbackButtonClickListener
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.RatingThreshold

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppRating.reset(this)
    }

    fun onDefaultExampleButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .setDebug(true)
            .showIfMeetsConditions()
    }

    fun onCustomIconButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        val iconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_star_black, null)

        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .setDebug(true)
            .setIconDrawable(iconDrawable)
            .showIfMeetsConditions()
    }

    fun onMailFeedbackButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .setDebug(true)
            .setMailSettingsForFeedbackDialog(
                MailSettings(
                    "info@your-app.de",
                    "Feedback Mail",
                    "This is an example text.\n\nYou could set some device infos here."
                )
            )
            .showIfMeetsConditions()
    }

    fun onCustomFeedbackButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .setDebug(true)
            .setUseCustomFeedback(true)
            .setCustomFeedbackButtonClickListener(object : CustomFeedbackButtonClickListener {
                override fun onClick(userFeedbackText: String) {
                    Toast.makeText(
                        this@MainActivity,
                        "Feedback: $userFeedbackText",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            .showIfMeetsConditions()
    }

    fun onShowNeverButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .setDebug(true)
            .showRateNeverButton()
            .showIfMeetsConditions()
    }

    fun onShowOnThirdClickButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .showRateNeverButton()
            .setMinimumLaunchTimes(3)
            .setMinimumDays(0)
            .setMinimumLaunchTimesToShowAgain(5)
            .setMinimumDaysToShowAgain(0)
            .showIfMeetsConditions()
    }

    fun onRatingThresholdButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .setDebug(true)
            .setRatingThreshold(RatingThreshold.FOUR_AND_A_HALF)
            .showIfMeetsConditions()
    }

    fun onFullStarRatingButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .setDebug(true)
            .setShowOnlyFullStars(true)
            .showIfMeetsConditions()
    }

    fun onCustomTextsButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        // This call is only needed in the example app. Just use the builder
        // directly within your app.
        val appRatingBuilder = resetSomeBuilderSettings(AppRating.Builder(this))

        appRatingBuilder
            .setDebug(true)
            .setRateNowButtonTextId(R.string.button_rate_now)
            .setRateLaterButtonTextId(R.string.button_rate_later)
            .showRateNeverButton(R.string.button_rate_never)
            .setTitleTextId(R.string.title_overview)
            .setMessageTextId(R.string.message_overview)
            .setConfirmButtonTextId(R.string.button_confirm)
            .setStoreRatingTitleTextId(R.string.title_store)
            .setStoreRatingMessageTextId(R.string.message_store)
            .setFeedbackTitleTextId(R.string.title_feedback)
            .setMailFeedbackMessageTextId(R.string.message_feedback)
            .setMailFeedbackButtonTextId(R.string.button_mail_feedback)
            .setNoFeedbackButtonTextId(R.string.button_no_feedback)
            .showIfMeetsConditions()
    }

    // This function is only needed in the example app. It's resetting some
    // configurations between the examples. Just use the builder directly
    // within your app.
    private fun resetSomeBuilderSettings(appRatingBuilder: AppRating.Builder): AppRating.Builder {
        return appRatingBuilder
            .setIconDrawable(null)
            .setRatingThreshold(RatingThreshold.THREE)
            .setShowOnlyFullStars(false)
            .setUseCustomFeedback(false)
    }
}
