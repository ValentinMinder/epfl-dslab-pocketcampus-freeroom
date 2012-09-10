package org.pocketcampus.tool.build.template

class ManifestTemplate {
	static def getText(String pluginManifests) {
		return  """\
<?xml version="1.0" encoding="utf-8"?>
<!-- AUTO-GENERATED POCKETCAMPUS MANIFEST FILE -->
<!-- changes to this file will be kindly disregarded -->

<manifest
	xmlns:android="http://schemas.android.com/apk/res/android"
	package="org.pocketcampus"
	android:installLocation="auto"
	android:versionCode="7"
	android:versionName="Third Version">

	<uses-sdk android:minSdkVersion="8"/>
	
	<application
		android:label="PocketCampus"
		android:theme="@style/PocketCampusTheme"
		android:icon="@drawable/app_icon"
		android:name="org.pocketcampus.android.platform.sdk.core.GlobalContext">
	
		"""+ pluginManifests +"""\

		<receiver android:name="com.google.android.apps.analytics.AnalyticsReceiver" 
		android:exported="true">
        	<intent-filter>
            	<action android:name="com.android.vending.INSTALL_REFERRER" />
            </intent-filter>
        </receiver>

	</application>
		
	<supports-screens android:smallScreens="true"
                  android:normalScreens="true"
                  android:largeScreens="true"
                  android:xlargeScreens="true" />

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

</manifest>
		"""
	}
}
