Bandwidth Voice Reference Android App
===============================

This project is a fully functioning Android app that leverages the BWsip framework to register users, and make and receive calls.

![App screenshot](https://github.com/bandwidthcom/catapult-reference-app-voice-android/blob/master/screenshot.png)

##Before you start
Before you get started, there are a few things that you will need to have set up first.
 - [Android Studio](http://developer.android.com/sdk/index.html)
 - [Android SDK set up and configured](http://developer.android.com/sdk/installing/index.html)
 - A [device](http://developer.android.com/tools/device.html) or an [emulator](http://developer.android.com/tools/devices/emulator.html) (ARMv7) to debug the app on
 - An account set up on the [Bandwidth Application Platform](https://catapult.inetwork.com/)

###Setting up your Bandwidth account
 - [Need to get detailed instructions for this part]
 
###Setting up your application server
 - [Need to get detailed instructions for this part]

##Get the code
Just grab this repository by running:

    $ git clone https://github.com/bandwidthcom/catapult-reference-app-voice-android.git

##Open the project
 - Launch Android Studio
 - Click File -> Import Project...
 - Select to the folder created by Git when you checked out the code

##Replace server URL
 - Open `app/src/main/res/values/strings.xml`
 - Replace the value for the `application_server_url` key with the URL of the server you set up in the steps above
 
> If you do not do this step, you will see an alert dialog when you try to open the app, and the app will not do anything else.

##Run the app
 - Click the Run button in Android Studio
 - Plug in your device or select an emulator image to start up
 - When the app launches, type in any username into the field and tap Register
 - After registration completes, a toast message will display your phone number
  - You can also see your phone number from the Account info menu item
 - You can now receive phone calls at this phone number
 - You can also dial a phone number and tap Call to place a call

> **Please note**: There is currently no background support so if you close the app or switch to a different app you will not receive incoming calls. If you're currently on a call and you switch to a different app bad things will probably happen.