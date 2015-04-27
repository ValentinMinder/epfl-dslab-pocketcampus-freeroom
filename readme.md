# PocketCampus

PocketCampus is the official EPFL app for students, collaborators and visitors.  
It is available for Android, iOS and Windows Phone.

## Prerequisites

Server:
* Java 8

Android client:
* [Android SDK tools](https://developer.android.com/sdk/index.html#Other):
    - Android Build Tools v22.0.1
    - Android Support Library & Android Support Repository
    - Google Play Services
    - Google Repository
    - Android 4.0.3 (API 15) platform
        - If you want to develop plugins using the Google Play Services on an emulator, install the latest Google APIs system image instead
    - Set the ANDROID_HOME environment variable to point to the SDK

iOS client:
* Xcode

Windows Phone client:
* Visual Studio 2013 (Community edition or above)
* Hyper-V support for the emulator, or a Windows Phone device

## Build instructions

The server and the Android projects are built with Gradle.  
To build the entire server, use `gradlew server:build`.  
To make a JAR with the server, use `gradlew server:shadowJar`, which creates a JAR in build/libs/.  
To merge all Android projects, use `gradlew android:merge`, which creates a project in android/ that you can then build like a normal Android project, e.g. `gradlew android:assembleDebug` and `gradlew android:assembleRelease` to assemble debug and release versions respectively.

The iOS and Windows Phone clients are built with Xcode and Visual Studio respectively.

## Other tasks

Take a look at the readme/ folder for detailed instructions on various subjects, such as adding a new plugin.
