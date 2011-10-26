package org.pocketcampus.tool.build.template

class ManifestTemplate {
	static def getText(String pluginManifests) {
		return  """\
<?xml version="1.0" encoding="utf-8"?>
<!-- AUTO-GENERATED POCKETCAMPUS MANIFEST FILE -->
<!-- your changes to this file will be kindly disregarded -->

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
