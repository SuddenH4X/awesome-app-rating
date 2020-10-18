package com.suddenh4x.ratingdialog.exampleapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.buttons.CustomFeedbackButtonClickListener
import com.suddenh4x.ratingdialog.preferences.MailSettings
import com.suddenh4x.ratingdialog.preferences.RatingThreshold

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppRating.reset(this)

        toastLiveData.observe(this) { toastString ->
            Toast.makeText(this, toastString, Toast.LENGTH_LONG).show()
        }
    }

    fun onResetButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.reset(this)
        Toast.makeText(this, R.string.toast_reset, Toast.LENGTH_SHORT).show()
    }

    fun onGoogleInAppReviewExampleButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
            .useGoogleInAppReview()
            .setGoogleInAppReviewCompleteListener { successful ->
                toastLiveData.postValue("Google in-app review completed (successful: $successful)")
            }
            .setDebug(true)
            .showIfMeetsConditions()
    }

    fun onDefaultExampleButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
            .setDebug(true)
            .showIfMeetsConditions()
    }

    fun onCustomIconButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        val iconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_star_black, null)

        AppRating.Builder(this)
            .setDebug(true)
            .setIconDrawable(iconDrawable)
            .showIfMeetsConditions()
    }

    fun onMailFeedbackButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
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
        AppRating.Builder(this)
            .setDebug(true)
            .setUseCustomFeedback(true)
            .setCustomFeedbackButtonClickListener(object : CustomFeedbackButtonClickListener {
                override fun onClick(userFeedbackText: String) {
                    toastLiveData.postValue("Feedback: $userFeedbackText")
                }
            })
            .showIfMeetsConditions()
    }

    fun onShowNeverButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
            .setDebug(true)
            .showRateNeverButton()
            .showIfMeetsConditions()
    }

    fun onShowNeverButtonAfterThreeTimesClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
            .setDebug(true)
            .showRateNeverButtonAfterNTimes(countOfLaterButtonClicks = 3)
            .showIfMeetsConditions()
    }

    fun onShowOnThirdClickButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
            .showRateNeverButton()
            .setMinimumLaunchTimes(3)
            .setMinimumDays(0)
            .setMinimumLaunchTimesToShowAgain(5)
            .setMinimumDaysToShowAgain(0)
            .showIfMeetsConditions()
    }

    fun onRatingThresholdButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
            .setDebug(true)
            .setRatingThreshold(RatingThreshold.FOUR_AND_A_HALF)
            .showIfMeetsConditions()
    }

    fun onFullStarRatingButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
            .setDebug(true)
            .setShowOnlyFullStars(true)
            .showIfMeetsConditions()
    }

    fun onCustomTextsButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        AppRating.Builder(this)
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

    companion object {
        // The livedata is used so that no context is given into the click listeners. (NotSerializableException)
        private val toastLiveData: MutableLiveData<String> = MutableLiveData()
    }
}
