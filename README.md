# WIP: Awesome App Rate
A highly customizable Android library providing a dialog, which asks the user to rate the app. If the user rates below the defined threshold, the dialog will show a feedback form or ask the user to mail his feedback. Otherwise it will ask the user to rate the app in the Google Play Store.

![showcase](https://github.com/SuddenH4X/awesome-app-rating/raw/feature/update_readme/preview/showcase.png)

## Features
- Auto fetches the app icon to use it in the dialog
- Let the dialog show up on a defined app session or after n days of usage
- Ask the user to mail his feedback or show a custom feedback form if the user rates below the defined minimum threshold
- All titles, messages and buttons are customizable
- You can override all click listeners to fit your needs
- The dialog handles orientation changes correctly
- Extracts the accent color of your app's theme

This library is:
- Completely written in Kotlin
- Unit and UI tested __(in progress)__
- AndroidX ready
- Easy debuggable
- Android Q (API 29) ready __(in progress)__
- Easy to use

## How to use
### Gradle
You can simply include the library via Gradle:

```groovy
dependencies {
    ...
    implementation 'com.suddenh4x.ratingdialog:awesome-app-rating:1.0.0'
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

If the rate, feedback or never button is clicked once, the dialog will never be shown again unless you reset the library settings with `AppRating.reset(this)`  - but this is not recommended.

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

- Change the title of the feedback dialog

```kotlin
.setFeedbackTitleTextId(feedbackTitleTextId: Int)
```

- Change the no feedback button text and add a click listener

```kotlin
.setNoFeedbackButton(noFeedbackButtonTextId: Int, noFeedbackButtonClickListener: RateDialogClickListener)
```

- Change the message of the mail feedback dialog (not visible if custom feedback is enabled)

```kotlin
.setMailFeedbackMessageTextId(feedbackMailMessageTextId: Int)
```

- Change the mail feedback button text and add a click listener (not visible if custom feedback is enabled)

```kotlin
.setMailFeedbackButton(mailFeedbackButtonTextId: Int, mailFeedbackButtonClickListener: RateDialogClickListener)
```

- Use the custom feedback dialog instead of the mail feedback dialog

```kotlin
.setUseCustomFeedback(useCustomFeedback: Boolean) // default is false
```

- Change the message of the custom feedback dialog (only visible if custom feedback is enabled)

```kotlin
.setCustomFeedbackMessageTextId(feedbackCustomMessageTextId: Int)
```

- Change the custom feedback button text and add a click listener (only visible if custom feedback is enabled)

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

- Enable debug mode which will cause the dialog to show up immediately when calling `showIfMeetsConditions()`  (no conditions will be checked)

```kotlin
.setDebug(isDebug: Boolean) // default is false
```

## Note
* Use `setRatingThreshold(RatingThreshold.NONE)` if you don't want to show the feedback form to the user

## Recommendations
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
