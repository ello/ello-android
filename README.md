![Ello + Android = Love](https://cloud.githubusercontent.com/assets/12459/13925727/0dc96a7a-ef4f-11e5-9fb0-b23a73551e7f.jpg)

# Ello Android
Ello's open source Android app

The app is a very simple wrapper around our React based web application. We handle a few of the rough edge cases but leave nearly all the functionality of the web app intact. Over time we will likely move portions of the experience over to native Android UI.

## Setup

Nothing much to it. Clone and sync gradle and you should be good to go.

## Notes

When creating the apk to upload to the Google Play Store it must be aligned.
`zipalign -f -v 4 app-release-unaligned.apk app-release-aligned.apk`

We are currently using a canary build of [CrossWalk](https://crosswalk-project.org). Canary builds are not available through remote Maven repositories so they must be downloaded `wget https://download.01.org/crosswalk/releases/crosswalk/android/canary/19.48.503.0/crosswalk-19.48.503.0.aar` and installed with Maven locally `mvn install:install-file -DgroupId=org.xwalk -DartifactId=xwalk_core_library_canary       -Dversion=19.48.503.0 -Dpackaging=aar  -Dfile=crosswalk-19.48.503.0.aar       -DgeneratePom=true`

## Contributing
Bug reports and pull requests are welcome on GitHub at https://github.com/ello/ello-android.

## License
Ello Android is released under the [MIT License](/LICENSE.txt)

## Code of Conduct
Ello was created by idealists who believe that the essential nature of all human beings is to be kind, considerate, helpful, intelligent, responsible, and respectful of others. To that end, we will be enforcing [the Ello rules](https://ello.co/wtf/policies/rules/) within all of our open source projects. If you don’t follow the rules, you risk being ignored, banned, or reported for abuse.
