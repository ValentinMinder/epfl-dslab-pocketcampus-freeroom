package org.pocketcampus.tool.build

class ManifestTemplate {
	static def getText(String pluginManifests) {
		return  """\
<?xml version="1.0" encoding="utf-8"?>
<!-- AUTO GENERATED POCKETCAMPUS MANIFEST FILE -->
<!-- DO NOT EDIT MANUALLY -->

<manifest
	xmlns:android="http://schemas.android.com/apk/res/android"
	package="org.pocketcampus.android.plugin.dashboard">

	<uses-sdk
		android:minSdkVersion="8" />
	
	<application
		android:label="@string/app_name"
		android:theme="@style/PocketCampusTheme"
		android:name="org.pocketcampus.android.platform.sdk.core.GlobalContext">
	
		"""+ pluginManifests +"""\
			
	</application>
	
</manifest>
		"""
	}
}
