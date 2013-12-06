# WhereToEat


WhereToEat is an app to find food and/or drinks around you, without having to search / select too many options with the best 
and limited results to make up your mind easily.

It also provides the informations (like website, phone number, directions to the place and reviews from other users).

<img src="http://i.imgur.com/wm8cQDX.png" height="300" />
&nbsp;&nbsp;
<img src="http://i.imgur.com/OcSr1Tk.png" height="300" />
&nbsp;&nbsp;
<img src="http://i.imgur.com/8r8a0i9.png" height="300" />
&nbsp;&nbsp;
<img src="http://i.imgur.com/xx4x9FB.png" height="300" />
&nbsp;&nbsp;
<img src="http://i.imgur.com/j36epC7.png" height="300" />
&nbsp;&nbsp;
<img src="http://i.imgur.com/Tly2VZv.png" height="300" />


## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)


## Building

The build requires [Gradle](http://www.gradleware.com/)
v1.6 and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

    export ANDROID_HOME=/opt/tools/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `gradle assemble` from the root directory to build the APK only
* Run `gradle build` from the root directory to build the app and also run
  the integration tests, this requires a connected Android device or running
  emulator.


## Acknowledgements

This project uses the 
* [Google Maps Android API v2](https://developers.google.com/maps/documentation/android/start) for the maps
* [Google Places API](https://developers.google.com/places/documentation/) for the data
* Also has included Yelp API support but not configured with app currently.


It also uses many other open source libraries such as:

 * [scribe-java](https://github.com/fernandezpablo85/scribe-java)
 * [Android Async HTTP](https://github.com/loopj/android-async-http)
 * [codepath-oauth](https://github.com/thecodepath/android-oauth-handler)
 * [UniversalImageLoader](https://github.com/nostra13/Android-Universal-Image-Loader)
 * [ActiveAndroid](https://github.com/pardom/ActiveAndroid)
 * [Gradle](https://github.com/gradle/gradle)

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/wheretoeatorg/WhereToEat/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.


