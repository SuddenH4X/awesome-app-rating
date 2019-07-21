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
                // needed to unset the icon drawable from the method below
                .setIconDrawable(null)
                .showIfMeetsConditions()
    }

    fun onCustomIconButtonClicked(view: View) {
        val iconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_star_black, null)

        AppRating.Builder(this)
                .setDebug(true)
                .setIconDrawable(iconDrawable)
                .showIfMeetsConditions()
    }

    fun onCustomFeedbackButtonClicked(view: View) {
        AppRating.Builder(this)
                .setDebug(true)
                // needed to unset the icon drawable from the method below
                .setIconDrawable(null)
                .setUseCustomFeedback(true)
                .showIfMeetsConditions()
    }

    fun onShowNeverButtonClicked(view: View) {
        AppRating.Builder(this)
                .setDebug(true)
                // needed to unset the icon drawable from the method below
                .setIconDrawable(null)
                .setRateLaterButton()
                .showRateNeverButton()
                .showIfMeetsConditions()
    }

    fun onShowOnThirdClickButtonClicked(view: View) {
        AppRating.Builder(this)
                // needed to unset the icon drawable from the method below
                .setIconDrawable(null)
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
                // needed to unset the icon drawable from the method below
                .setIconDrawable(null)
                .setRatingThreshold(RatingThreshold.FOUR_AND_A_HALF)
                .showIfMeetsConditions()
    }
}
