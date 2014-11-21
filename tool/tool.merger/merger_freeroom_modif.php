This file contains the change to make to merger.php in order to make FreeRoom android client work with the rest of PocketCampus android client.
The real file is not modified to avoid git conflicts. Merge must be done manually after pull request time, to integrate FreeRoom android client.

// lines 18-26, added ", "joda-time-2.3.jar", "PopupMenuCompatLib.jar");"
$libs_to_export = array(
		"android-support-v4.jar",
		"commons-io-2.0.1.jar", "commons-lang-2.6.jar", "commons-lang3-3.0.1.jar", 
		"gcm.jar", 
		"libGoogleAnalytics.jar", 
		"osmdroid-android-3.0.3.jar", 
		"servlet-api-3.0.jar", "slf4j-api-1.6.2.jar", 
		"universal-image-loader-1.8.0.jar", "joda-time-2.3.jar",
		"PopupMenuCompatLib.jar");

		// line 347, added " || $entry == 'tests')"
		if ($entry == '.' || $entry == '..' || $entry == 'tests') {
			continue;
		}
