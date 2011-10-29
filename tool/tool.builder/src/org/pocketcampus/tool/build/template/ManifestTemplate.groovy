package org.pocketcampus.tool.build.template

class ManifestTemplate {
	static def getText(String pluginManifests) {
		return  """\
<?xml version="1.0" encoding="utf-8"?>
<!-- AUTO-GENERATED POCKETCAMPUS MANIFEST FILE -->
<!-- changes to this file will be kindly disregarded -->

<manifest
	xmlns:android="http://schemas.android.com/apk/res/android"
	package="org.pocketcampus">

	<uses-sdk
		android:minSdkVersion="8" />
	
	<application
		android:label="@string/app_name"
		android:theme="@style/PocketCampusTheme"
		android:name="org.pocketcampus.android.platform.sdk.core.GlobalContext">
	
		"""+ pluginManifests +"""\
			
	</application>

	<permission
        android:name="org.pocketcampus.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />

    <uses-permission android:name="org.pocketcampus.permission.C2D_MESSAGE" />
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="android.permission.USE_CREDENTIALS" />
    <uses-permission android:name="android.permission.VIBRATE" />
	
</manifest>
		"""
	}
}
