Bandwidth Voice Reference Android App
===============================

This project is a fully functioning Android app that leverages the Acrobits SDK to register users, and make and receive calls. Acrobits provides a fully featured commercial SDK for mobile applications that wish to integrate voice capabilities and is fully compatible with Bandwidth's endpoint.

![App screenshot](https://github.com/bandwidthcom/catapult-reference-app-voice-android/blob/master/screenshot.png)

This application comes with a trial version of the Acrobits SDK that terminate calls after 1 minute.

##Before you start
Before you get started, there are a few things that you will need to have set up first.
 - [Android Studio](http://developer.android.com/sdk/index.html)
 - [Android SDK set up and configured](http://developer.android.com/sdk/installing/index.html)
 - A [device](http://developer.android.com/tools/device.html) or an [emulator](http://developer.android.com/tools/devices/emulator.html) to debug the app on
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

##Set up push notifications

In order to be able to receive incoming calls via push notifications, you will need to set up the application to register for push notifications and also
a server that will keep your SIP registration active while the application is on background. This server will send a push notification when an incoming call arrives.

To set up the server, you will need to get 2 server packages from Acrobits: `pnmediator` and `SIPIS`. These 2 servers need to be installed on a Debian machine.

###Server set up

Follow [this](https://doc.acrobits.net/sipis/installation.html) guide to install SIPIS. You will need to ask Acrobits for a username and password to download the package.

To install pnmediator, simply install the Debian package provided by Acrobits. 
To configure it, you will need to copy an example configuration file and change it to add your GCM api key:

    sudo cp /etc/pnmediator2/settings/com.example.android /etc/pnmediator2/settings/com.bandwidth.androidreference
    sudo nano /etc/pnmediator2/settings/com.bandwidth.androidreference

Change `ApiKey=""` to use your GCM API key, then restart the pnmediator service:

    sudo systemctl restart pnmediator2.socket

Now that we have pnmediator installed, we need to configure SIPIS to use it. Edit `/etc/sipis/Settings.xml` and chenge the `Host` and `Port` and `RequiresTls` on `NotificationServer`:

    <NotificationServers>
        <NotificationServer
            Name="*"
            Host="localhost"
            Port="5662"
            Premium="No"
            RequiresTls="No">
        </NotificationServer>
    </NotificationServers>

Save the file and restart SIPIS:

    sudo systemctl restart sipis

After this, your servers are ready to be used by the application.

###Application set up

To set up the application, follow these steps to create your credentials on Firebase/GCM and download the configuration file to `app/google-services.json`:

 - Create a Firebase project in the [Firebase console](https://console.firebase.google.com/).
 - Click Add Firebase to your Android app and follow the setup steps.
 - When prompted, enter your app's package name (you can change this app's package name if necessary). It's important to enter the package name your app is using; this can only be set when you add an app to your Firebase project.
 - At the end, you'll download a `google-services.json` file. You can download this file again at any time.
 - If you haven't done so already, copy this into your app/ folder.

The application will take care of registering the device if this file is present.

Replace the `push_server_host` variable on `app/src/main/res/values/strings.xml` with your push server's hostname.

##Run the app
 - Click the Run button in Android Studio
 - Plug in your device or select an emulator image to start up
 - When the app launches, type in any username into the field and tap Register
 - After registration completes, a toast message will display your phone number
  - You can also see your phone number from the Account info menu item
 - You can now receive phone calls at this phone number
 - You can also dial a phone number and tap Call to place a call

## Adding voice support to your own app

Please contact Acrobits for a commercial version of the SDK. They will provide you with a license and detailed instructions on how to integrate the SDK into your application.

If you only want to try it out, copy the file on ```app/libs/libSoftphone-debug.aar``` and add it as a dependency:

```
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    compile(name: 'libSoftphone-debug', ext: 'aar')
}
```

You can then use the code provided by this sample app as the basis for your own implementation.

More Acrobits resources:

[Provide links to Acrobits]
