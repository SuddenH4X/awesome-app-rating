# Awesome App Rating

[![Build Status](https://app.bitrise.io/app/3156ba39c3e19393/status.svg?token=xXPr-IjWuLLKVliX5QPZKg&branch=master)](https://app.bitrise.io/app/3156ba39c3e19393)

A highly customizable Android library providing a dialog, which asks the user to rate the app. If the user rates below the defined threshold, the dialog will show a feedback form or ask the user to mail his feedback. Otherwise it will ask the user to rate the app in the Google Play Store.

![showcase](https://github.com/SuddenH4X/awesome-app-rating/raw/develop/preview/showcase.png)

## Features
- Auto fetches the app icon to use it in the dialog
- Let the dialog show up on a defined app session or after n days of usage
- Ask the user to mail his feedback or show a custom feedback form if the user rates below the defined minimum threshold
- All titles, messages and buttons are customizable
- You can override all click listeners to fit your needs
- The dialog handles orientation changes correctly
- Extracts the accent color of your app's theme

This library:
- is completely written in Kotlin
- is Unit tested
- uses AndroidX
- uses no third party dependencies
- is easy debuggable
- is Android Q (API 29) ready
- is easy to use

## How to use
### Gradle
The library supports API level 14 and higher. You can simply include it in your app via Gradle:

```groovy
dependencies {
    ...
    implementation 'com.suddenh4x.ratingdialog:awesome-app-rating:1.2.0'
}
```

### Builder usage
This library provides a builder to configure its behavior. 
```kotlin
AppRating.Builder(this)
	.setMinimumLaunchTimes(5)
	.setMinimumDays(7)
	.setMinimumLaunchTimesToShowAgain(5)
	.setMinimumDaysToShowAgain(10)
	.setRatingThreshold(RatingThreshold.FOUR)
	.showIfMeetsConditions()
```
You should call the builder only in the `onCreate()` method of your main Activity class, because every call of the method `showIfMeetsConditions` will increase the launch times.

With the settings above the dialog will show up if the following conditions are met:

- The app is installed for a minimum of 7 days and 
- the app is launched for a minimum of 5 times

Furthermore the dialog will show up again if the user has clicked the `later` button of the dialog and

- The button click happened at least 10 days ago and 
- the app is launched again for a minimum of 5 times.

If the rate or never button is clicked once or if the user rates below the defined minimum threshold, the dialog will never be shown again unless you reset the library settings with `AppRating.reset(this)`  - but this is not recommended.

If you have adjusted the dialog to suit your preferences, you have multiple possibilities to show it. Usually you want to show the dialog if the configured conditions are met:

```kotlin
ratingBuilder.showIfMeetsConditions()
```

But you can also just create the dialog to show it later

```kotlin
ratingBuilder.create()
```

or you can show it immediately:

```kotlin
ratingBuilder.showNow()
```

### Configuration

Between the constructor and the show or create method you can adjust the dialog to suit your preferences. You have the following options:

#### When to show up

- Change the number of days the app has to be installed

```kotlin
.setMinimumDays(minimumDays: Int) // default is 3
```

- Change the minimum number of app launches

```kotlin
.setMinimumLaunchTimes(launchTimes: Int) // default is 5
```

- Change the number of days that must have passed away after the last `later` button click

```kotlin
.setMinimumDaysToShowAgain(minimumDaysToShowAgain: Int) // default is 14
```

- Change the minimum number of app launches after the last `later` button click

```kotlin
.setMinimumLaunchTimesToShowAgain(launchTimesToShowAgain: Int) // default is 5
```

#### Design

##### General

- Change the icon of the dialog

```kotlin
.setIconDrawable(iconDrawable: Drawable?) // default is null which means app icon
```

- Change the rate later button text and add a click listener

```kotlin
.setRateLaterButton(rateLaterButtonTextId: Int, onRateLaterButtonClickListener: RateDialogClickListener)
```

- Show the rate never button, change the button text and add a click listener

```kotlin
.showRateNeverButton(rateNeverButtonTextId: Int, onRateNeverButtonClickListener: RateDialogClickListener) // by default the button is hidden
```

##### Rating Overview

- Change the title of the rating dialog

```kotlin
.setTitleTextId(titleTextId: Int)
```

- Add a message to the rating dialog

```kotlin
.setMessageTextId(messageTextId: Int) // by default no message is shown
```

- Change the confirm button text

```kotlin
.setConfirmButtonTextId(confirmButtonTextId: Int)
```

##### Store Rating

- Change the title of the store rating dialog

```kotlin
.setStoreRatingTitleTextId(storeRatingTitleTextId: Int)
```

- Change the message of the store rating dialog

```kotlin
.setStoreRatingMessageTextId(storeRatingMessageTextId: Int)
```

- Change the rate now button text

```kotlin
.setRateNowButtonTextId(rateNowButtonTextId: Int)
```

- Override the rate now button click listener

````kotlin
.setRateNowButtonClickListener(rateNowButtonClickListener: RateDialogClickListener) // by default it opens the play store listing of your app
````

##### Feedback

- Change the title of the feedback dialog

```kotlin
.setFeedbackTitleTextId(feedbackTitleTextId: Int)
```

- Change the no feedback button text and add a click listener

```kotlin
.setNoFeedbackButton(noFeedbackButtonTextId: Int, noFeedbackButtonClickListener: RateDialogClickListener)
```

- Use the custom feedback dialog instead of the mail feedback dialog

```kotlin
.setUseCustomFeedback(useCustomFeedback: Boolean) // default is false
```

##### Mail Feedback

If custom feedback is enabled, these settings will be ignored:

- Change the message of the mail feedback dialog

```kotlin
.setMailFeedbackMessageTextId(feedbackMailMessageTextId: Int)
```

- Set the mail settings for the mail feedback dialog (mail address, subject, text and app chooser title)

```kotlin
setMailSettingsForFeedbackDialog(mailSettings: MailSettings)
```

- Change the mail feedback button text and add a click listener (setting a click listener will overwrite the mail settings)

```kotlin
.setMailFeedbackButton(mailFeedbackButtonTextId: Int, mailFeedbackButtonClickListener: RateDialogClickListener)
```

##### Custom Feedback

These settings will only apply if custom feedback is enabled:

- Change the message of the custom feedback dialog

```kotlin
.setCustomFeedbackMessageTextId(feedbackCustomMessageTextId: Int)
```

- Change the custom feedback button text and add a click listener

```kotlin
.setCustomFeedbackButton(customFeedbackButtonTextId: Int, customFeedbackButtonClickListener: CustomFeedbackButtonClickListener)
```

#### Other settings

- Choose the rating threshold. If the user rates below, the feedback dialog will show up. If the user rates the threshold or higher, the store dialog will show up.

```kotlin
.setRatingThreshold(ratingThreshold: RatingThreshold) // default is RatingThreshold.THREE
```

- Choose if the dialogs should be cancelable (by clicking outside or using the back button)

```kotlin
.setCancelable(cancelable: Boolean) // default is false
```

- Disable all library logs

```kotlin
.setLoggingEnabled(isLoggingEnabled: Boolean) // default is true
```

- Enable debug mode, which will cause the dialog to show up immediately when calling `showIfMeetsConditions()`  (no conditions will be checked)

```kotlin
.setDebug(isDebug: Boolean) // default is false
```

### Orientation Change

If the orientation is changed, the `onCreate()` method will be called again and so does the Builder. These additional calls will distort the library behavior because each call of `showIfMeetsConditions()` will increase the counted app launches. To guarantee the correct behavior, you have to check for the `savedInstanceState` like this:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (savedInstanceState == null) {
        AppRating.Builder(this)
                // your configuration
                .showIfMeetsConditions()
    }
}
```

## Note

* Don't forget to set up the mail settings if you want to use the mail feedback dialog (otherwise nothing will happen)
* Use `setRatingThreshold(RatingThreshold.NONE)` if you don't want to show the feedback form to the user
* If you set  `setUseCustomFeedback()` to `true`, you have to handle the feedback text by yourself by adding a click listener (`setCustomFeedbackButton()`)
* If the user rates below the defined minimum threshold, the feedback dialog will be displayed and then the dialog will not show up again
* If you don't want to customize anything, you can just use `AppRating.Builder(this).showIfMeetsConditions()` without any settings
* If you have any problems, check out the logs in Logcat (You can filter by `awesome_app_rating`)
* Look at the example app to get first impressions

## Recommendations
The following things are highly recommended to not annoy the user, which in turn could lead to negative reviews:

- Don't show the dialog immediately after install
- Don't set the rating threshold to 5
- Show the `Never` button so the user can decide whether or not to rate your app
- Don't use `AppRating.reset(this)` in your production app

## License

```
Copyright (C) 2019 SuddenH4X

Licensed under the GNU General Public License v3.0. 
You may not use this file except in compliance with the License. 
You may obtain a copy of the License at:

https://www.gnu.org/licenses/gpl-3.0.en.html

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
