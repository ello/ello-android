![Ello + Android = Love](https://cloud.githubusercontent.com/assets/12459/13925727/0dc96a7a-ef4f-11e5-9fb0-b23a73551e7f.jpg)

# Ello Android
Ello's open source Android app

The app is a very simple wrapper around our React based web application. We handle a few of the rough edge cases but leave nearly all the functionality of the web app intact. Over time we will likely move portions of the experience over to native Android UI.

## Setup

Nothing much to it.

1. Clone the repo `git clone git@github.com:ello/ello-android.git`
- Download and install [Android Studio](https://developer.android.com/studio/index.html)
- Follow the setup wizard
  * Standard
    - Use this if you don't already have the SDK downloaded
  * Custom
    1. Use this if you use the android SDK manager or Homebrew for SDK versions
      as you will be able to modify the sdk path and can minimize what will get
      downloaded initially
    - Pick a light/dark theme
    - Select path to an existing sdk like `usr/local/Cellar/android-sdk`
    - Probably go with the recommended emulator settings
    - Finish to download required SDK components
- Open the project folder to install/update gradle

## Notes

When creating the apk to upload to the Google Play Store it must be aligned.
`zipalign -f -v 4 app-release-unaligned.apk app-release-aligned.apk`

## Contributing
Bug reports and pull requests are welcome on GitHub at https://github.com/ello/ello-android.

## License
Ello Android is released under the [MIT License](/LICENSE.txt)

## Code of Conduct
Ello was created by idealists who believe that the essential nature of all human beings is to be kind, considerate, helpful, intelligent, responsible, and respectful of others. To that end, we will be enforcing [the Ello rules](https://ello.co/wtf/policies/rules/) within all of our open source projects. If you don’t follow the rules, you risk being ignored, banned, or reported for abuse.
