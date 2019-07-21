# WIP: Awesome App Rate
A highly customizable Android library providing a dialog, which asks the user to rate the app. If the user rates below the defined threshold, the dialog will show a feedback form or ask the user to mail his feedback. Otherwise it will ask the user to rate the app in the Google Play Store.

# Features
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

# How to use
If you want the dialog to appear on the Nth session, just add `setMinimumLaunchTimes(N)` to the dialog builder method and call it in the `onCreate()` method of your main Activity class. The dialog will appear when the app is opened for the Nth time.

# Note

# Recommendations

# License
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
