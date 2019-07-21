package suddenh4x.com.exampleapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.suddenh4x.awesome_app_rate.AppRating
import com.suddenh4x.awesome_app_rate.preferences.RatingThreshold

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppRating.reset(this)
    }

    fun onDefaultExampleButtonClicked(view: View) {
        AppRating.Builder(this)
                .setDebug(true)
                // This is needed to unset the icon drawable. You don't need to use this in your app.
                .setIconDrawable(null)
                // This is needed to unset the custom feedback. You don't need to use this in your app.
                .setUseCustomFeedback(false)
                .showIfMeetsConditions()
    }

    fun onCustomIconButtonClicked(view: View) {
        val iconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_star_black, null)

        AppRating.Builder(this)
                .setDebug(true)
                // This is needed to unset the custom feedback. You don't need to use this in your app.
                .setUseCustomFeedback(false)
                .setIconDrawable(iconDrawable)
                .showIfMeetsConditions()
    }

    fun onCustomFeedbackButtonClicked(view: View) {
        AppRating.Builder(this)
                .setDebug(true)
                // This is needed to unset the icon drawable. You don't need to use this in your app.
                .setIconDrawable(null)
                .setUseCustomFeedback(true)
                .showIfMeetsConditions()
    }

    fun onShowNeverButtonClicked(view: View) {
        AppRating.Builder(this)
                .setDebug(true)
                // This is needed to unset the icon drawable. You don't need to use this in your app.
                .setIconDrawable(null)
                // This is needed to unset the custom feedback. You don't need to use this in your app.
                .setUseCustomFeedback(false)
                .setRateLaterButton()
                .showRateNeverButton()
                .showIfMeetsConditions()
    }

    fun onShowOnThirdClickButtonClicked(view: View) {
        AppRating.Builder(this)
                // This is needed to unset the icon drawable. You don't need to use this in your app.
                .setIconDrawable(null)
                // This is needed to unset the custom feedback. You don't need to use this in your app.
                .setUseCustomFeedback(false)
                .setRateLaterButton()
                .showRateNeverButton()
                .setMinimumLaunchTimes(3)
                .setMinimumDays(0)
                .setMinimumLaunchTimesToShowAgain(5)
                .setMinimumDaysToShowAgain(0)
                .showIfMeetsConditions()
    }

    fun onRatingThresholdButtonClicked(view: View) {
        AppRating.Builder(this)
                .setDebug(true)
                // This is needed to unset the icon drawable. You don't need to use this in your app.
                .setIconDrawable(null)
                // This is needed to unset the custom feedback. You don't need to use this in your app.
                .setUseCustomFeedback(false)
                .setRatingThreshold(RatingThreshold.FOUR_AND_A_HALF)
                .showIfMeetsConditions()
    }
}
