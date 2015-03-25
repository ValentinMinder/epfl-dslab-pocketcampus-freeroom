<?php
/**
  ANDROID CLIENT MERGER

  This script merges the PocketCampus android client
  It transforms all the plugins into a single project
  that you can build using ANT or imoprt it in eclipse

  You should specify $plugins_to_merge and $libs_to_export

  @Author: Amer C (amer.chamseddine@epfl.ch)
*/

chdir(dirname(__FILE__));

$plugins_to_merge = array("Dashboard", "Camipro", "Moodle", "Authentication", "Food", "Transport", "News", "Map", "Directory", "PushNotif", "Events", "CloudPrint", "IsAcademia", "RecommendedApps", "FreeRoom");

$libs_to_export = array(
		"android-support-v4.jar",
		"commons-io-2.0.1.jar", "commons-lang-2.6.jar", "commons-lang3-3.0.1.jar",
		"gcm.jar",
		"httpclient-4.3.5.jar", "httpcore-4.3.2.jar", "httpmime-4.3.5.jar",
		"libGoogleAnalyticsServices.jar", "libthrift-0.9.2.jar", "javax.annotation.jar", 
		"popup-menu-compat-lib.jar",
		"servlet-api-3.0.jar", "slf4j-api-1.6.2.jar", 
		"universal-image-loader-1.9.3.jar");

$path_to_plugin_dir = "../../plugin";
$path_to_platform_dir = "../../platform";
$path_to_lib_dir = "../../platform/platform.android/libs";

$versionCode = "30";
$versionName = "2.4";

function import_nodes($file, $tag, $doc, $parent_node, $nodes_to_remove) {
	$doc2 = new DOMDocument();
	$doc2->preserveWhiteSpace = false;
	$doc2->loadXML(file_get_contents($file));
	//print_r($doc2->saveXML());

	if($nodes_to_remove) {
		$xpath = new DOMXPath($doc2);
		$data = $xpath->query($nodes_to_remove);
		foreach($data as $node) {
			$node->parentNode->removeChild($node);
		}
	}

	$xpath = new DOMXPath($doc2);
	$data = $xpath->query($tag);
	//print_r($doc2->getElementsByTagName("//activity"));
	foreach($data as $node) {
		//print_r($doc2->saveXML($node));
		$n = $doc->importNode($node, true);
		//$n->removeAttribute("xmlns:android");
		$parent_node->appendChild($n);
	}
}


function generate_android_manifest($output_dir, $is_lib){
	global $plugins_to_merge;
	global $path_to_plugin_dir;

	global $versionCode;
	global $versionName;

	$doc = new DOMDocument("1.0", "utf-8");
	$doc->formatOutput = true;

	$manif = $doc->createElement("manifest");
	$doc->appendChild($manif);
	$manif->setAttribute("xmlns:android", "http://schemas.android.com/apk/res/android");
	$manif->setAttribute("package", "org.pocketcampus");
	$manif->setAttribute("android:versionCode", "$versionCode");
	$manif->setAttribute("android:versionName", "$versionName");

	$app = $doc->createElement("application");
	$manif->appendChild($app);
	$app->setAttribute("android:label", "@string/app_name");
	$app->setAttribute("android:theme", "@style/PocketCampusActionBarTheme");
	$app->setAttribute("android:icon", "@drawable/app_icon");
	$app->setAttribute("android:name", "org.pocketcampus.platform.android.core.GlobalContext");

	$sdk = $doc->createElement("uses-sdk");
	$manif->appendChild($sdk);
	$sdk->setAttribute("android:minSdkVersion", "15");

	if(!$is_lib) {
		foreach($plugins_to_merge as $plgn) {
			$plugin = strtolower($plgn);
			echo "Merging ".$plugin."\n";
			$manifest_file = "$path_to_plugin_dir/$plugin/plugin.$plugin.android/AndroidManifest.xml";
			import_nodes($manifest_file, "/manifest/application/activity", $doc, $app, ($plugin == "dashboard" ? "" : "//category[@android:name='android.intent.category.LAUNCHER']"));
			import_nodes($manifest_file, "/manifest/application/service", $doc, $app, "");
			import_nodes($manifest_file, "/manifest/application/receiver", $doc, $app, "");

			import_nodes($manifest_file, "/manifest/permission-group", $doc, $manif, "");
			import_nodes($manifest_file, "/manifest/permission", $doc, $manif, "");
			import_nodes($manifest_file, "/manifest/uses-permission", $doc, $manif, "");

			if($plugin == "map"){
				$keyMetaData = $doc->createElement("meta-data");
				$keyMetaData->setAttribute("android:name", "com.google.android.maps.v2.API_KEY");
				$keyMetaData->setAttribute("android:value", "AIzaSyBxjrH9IRyVCTJrHdcTtNGIKtuZSlXcRTE");
				$app->appendChild($keyMetaData);
				$keyMetaData = $doc->createElement("meta-data");
				$keyMetaData->setAttribute("android:name", "com.google.android.gms.version");
				$keyMetaData->setAttribute("android:value", "@integer/google_play_services_version");
				$app->appendChild($keyMetaData);
			}
		}
	}

	file_put_contents("$output_dir/AndroidManifest.xml", $doc->saveXML());
	//print_r($doc->saveXML());
}

function generate_ant_properties($output_dir){
	$content = "";
	$content .= "key.store=pocketcampus-key.keystore\n";
	$content .= "key.alias=mykey\n";
	//$content .= "key.store.password=XXXXXXXXXXXXXXXXXXXXXXXXX\n";
	//$content .= "key.alias.password=XXXXXXXXXXXXXXXXXXXXXXXXX\n";
	// If you dont specify passwords here, you will be asked to provide them on the command line. Great!
	file_put_contents("$output_dir/ant.properties", $content);
}

function generate_build_xml($output_dir, $project_name){
	$doc = new DOMDocument("1.0", "UTF-8");
	$doc->formatOutput = true;

	$proj = $doc->createElement("project");
	$doc->appendChild($proj);
	$proj->setAttribute("name", $project_name);
	$proj->setAttribute("default", "help");

	$prop = $doc->createElement("property");
	$proj->appendChild($prop);
	$prop->setAttribute("file", "local.properties");

	$prop = $doc->createElement("property");
	$proj->appendChild($prop);
	$prop->setAttribute("file", "ant.properties");

	$prop = $doc->createElement("property");
	$proj->appendChild($prop);
	$prop->setAttribute("environment", "env");

	$cond = $doc->createElement("condition");
	$proj->appendChild($cond);
	$cond->setAttribute("property", "sdk.dir");
	$cond->setAttribute("value", "\${env.ANDROID_HOME}");

	$isset = $doc->createElement("isset");
	$cond->appendChild($isset);
	$isset->setAttribute("property", "env.ANDROID_HOME");

	$lprop = $doc->createElement("loadproperties");
	$proj->appendChild($lprop);
	$lprop->setAttribute("srcFile", "project.properties");

	$fail = $doc->createElement("fail");
	$proj->appendChild($fail);
	$fail->setAttribute("message", "sdk.dir is missing. Make sure to generate local.properties using 'android update project' or to inject it through an env var");
	$fail->setAttribute("unless", "sdk.dir");

	$import = $doc->createElement("import");
	$proj->appendChild($import);
	$import->setAttribute("file", "\${sdk.dir}/tools/ant/build.xml");

	$import = $doc->createElement("import");
	$proj->appendChild($import);
	$import->setAttribute("file", "custom_rules.xml");
	$import->setAttribute("optional", "true");

	file_put_contents("$output_dir/build.xml", $doc->saveXML());
}

function generate_proguard_cfg($output_dir){
	$content = <<<EOS
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable\$Creator *;
}
EOS;
	file_put_contents("$output_dir/proguard.cfg", $content);
}

function generate_project_properties($output_dir, $is_lib, $refs){
	$content = "target=android-15\n";
	if($is_lib)
		$content .= "android.library=true\n";
	$i = 1;
	foreach($refs as $ref)
		$content .= "android.library.reference." . $i++ . "=$ref\n";
	file_put_contents("$output_dir/project.properties", $content);
}

function generate_dot_classpath($output_dir){
	global $libs_to_export;

	$doc = new DOMDocument("1.0", "utf-8");
	$doc->formatOutput = true;

	$cp = $doc->createElement("classpath");
	$doc->appendChild($cp);

	$cpe = $doc->createElement("classpathentry");
	$cp->appendChild($cpe);
	$cpe->setAttribute("kind", "src");
	$cpe->setAttribute("path", "src");

	$cpe = $doc->createElement("classpathentry");
	$cp->appendChild($cpe);
	$cpe->setAttribute("kind", "src");
	$cpe->setAttribute("path", "gen");

	$cpe = $doc->createElement("classpathentry");
	$cp->appendChild($cpe);
	$cpe->setAttribute("kind", "con");
	$cpe->setAttribute("path", "com.android.ide.eclipse.adt.ANDROID_FRAMEWORK");

	$cpe = $doc->createElement("classpathentry");
	$cp->appendChild($cpe);
	$cpe->setAttribute("kind", "con");
	$cpe->setAttribute("path", "com.android.ide.eclipse.adt.LIBRARIES");

	foreach($libs_to_export as $lib) {
		$cpe = $doc->createElement("classpathentry");
		$cp->appendChild($cpe);
		$cpe->setAttribute("kind", "lib");
		$cpe->setAttribute("path", "libs/$lib");
	}

	$cpe = $doc->createElement("classpathentry");
	$cp->appendChild($cpe);
	$cpe->setAttribute("kind", "output");
	$cpe->setAttribute("path", "bin/classes");

	file_put_contents("$output_dir/.classpath", $doc->saveXML());
}

function generate_dot_project($output_dir, $project_name){
	$doc = new DOMDocument("1.0", "utf-8");
	$doc->formatOutput = true;

	$projectDescription = $doc->createElement("projectDescription");
	$doc->appendChild($projectDescription);

	$name = $doc->createElement("name");
	$name->appendChild($doc->createTextNode($project_name));
	$projectDescription->appendChild($name);
	$comment = $doc->createElement("comment");
	$comment->appendChild($doc->createTextNode(""));
	$projectDescription->appendChild($comment);
	$projects = $doc->createElement("projects");
	$projects->appendChild($doc->createTextNode(""));
	$projectDescription->appendChild($projects);

	$buildSpec = $doc->createElement("buildSpec");
	$projectDescription->appendChild($buildSpec);

	$buildCommand = $doc->createElement("buildCommand");
	$buildSpec->appendChild($buildCommand);
	$name = $doc->createElement("name");
	$name->appendChild($doc->createTextNode("com.android.ide.eclipse.adt.ResourceManagerBuilder"));
	$buildCommand->appendChild($name);
	$arguments = $doc->createElement("arguments");
	$arguments->appendChild($doc->createTextNode(""));
	$buildCommand->appendChild($arguments);
	$buildCommand = $doc->createElement("buildCommand");
	$buildSpec->appendChild($buildCommand);
	$name = $doc->createElement("name");
	$name->appendChild($doc->createTextNode("com.android.ide.eclipse.adt.PreCompilerBuilder"));
	$buildCommand->appendChild($name);
	$arguments = $doc->createElement("arguments");
	$arguments->appendChild($doc->createTextNode(""));
	$buildCommand->appendChild($arguments);
	$buildCommand = $doc->createElement("buildCommand");
	$buildSpec->appendChild($buildCommand);
	$name = $doc->createElement("name");
	$name->appendChild($doc->createTextNode("org.eclipse.jdt.core.javabuilder"));
	$buildCommand->appendChild($name);
	$arguments = $doc->createElement("arguments");
	$arguments->appendChild($doc->createTextNode(""));
	$buildCommand->appendChild($arguments);
	$buildCommand = $doc->createElement("buildCommand");
	$buildSpec->appendChild($buildCommand);
	$name = $doc->createElement("name");
	$name->appendChild($doc->createTextNode("com.android.ide.eclipse.adt.ApkBuilder"));
	$buildCommand->appendChild($name);
	$arguments = $doc->createElement("arguments");
	$arguments->appendChild($doc->createTextNode(""));
	$buildCommand->appendChild($arguments);

	$natures = $doc->createElement("natures");
	$projectDescription->appendChild($natures);

	$nature = $doc->createElement("nature");
	$nature->appendChild($doc->createTextNode("com.android.ide.eclipse.adt.AndroidNature"));
	$natures->appendChild($nature);
	$nature = $doc->createElement("nature");
	$nature->appendChild($doc->createTextNode("org.eclipse.jdt.core.javanature"));
	$natures->appendChild($nature);

	file_put_contents("$output_dir/.project", $doc->saveXML());
}


/**
 * Copy a file, or recursively copy a folder and its contents
 *
 * @author      Aidan Lister <aidan@php.net>
 * @version     1.0.1
 * @link        http://aidanlister.com/2004/04/recursively-copying-directories-in-php/
 * @param       string   $source    Source path
 * @param       string   $dest      Destination path
 * @return      bool     Returns TRUE on success, FALSE on failure
 */
function copyr($source, $dest) {
	// Check for symlinks
	if (is_link($source)) {
		return symlink(readlink($source), $dest);
	}

	// Simple copy for a file
	if (is_file($source)) {
		return copy($source, $dest);
	}

	// Make destination directory
	if (!is_dir($dest)) {
		mkdir($dest);
	}

	// Loop through the folder
	$dir = dir($source);
	while (false !== $entry = $dir->read()) {
		// Skip pointers
		if ($entry == '.' || $entry == '..') {
			continue;
		}

		// Deep copy directories
		copyr("$source/$entry", "$dest/$entry");
	}

	// Clean up
	$dir->close();
	return true;
}

function delete_dir($path) {
	return is_file($path) ? @unlink($path) : array_map(__FUNCTION__, glob($path.'/*')) == @rmdir($path);
}

function collect_res($output_dir) {
	global $plugins_to_merge;
	global $path_to_plugin_dir;
	global $path_to_platform_dir;

	copyr("$path_to_platform_dir/platform.android/res", "$output_dir/res");

	foreach($plugins_to_merge as $plgn) {
		$plugin = strtolower($plgn);
		copyr("$path_to_plugin_dir/$plugin/plugin.$plugin.android/res", "$output_dir/res");
	}

}

function collect_src($output_dir) {
	global $plugins_to_merge;
	global $path_to_plugin_dir;
	global $path_to_platform_dir;

	copyr("$path_to_platform_dir/platform.android/src", "$output_dir/src");
	copyr("$path_to_platform_dir/platform.shared/src", "$output_dir/src");

	foreach($plugins_to_merge as $plgn) {
		$plugin = strtolower($plgn);
		if(is_dir("$path_to_plugin_dir/$plugin/plugin.$plugin.android/src")) // if has .android proj
			copyr("$path_to_plugin_dir/$plugin/plugin.$plugin.android/src", "$output_dir/src");
		if(is_dir("$path_to_plugin_dir/$plugin/plugin.$plugin.shared/src")) // if has .shared proj
			copyr("$path_to_plugin_dir/$plugin/plugin.$plugin.shared/src", "$output_dir/src");
	}

}

function export_libs($output_dir) {
	global $libs_to_export;
	global $path_to_lib_dir;

	if(!is_dir("$output_dir/libs")) {
		mkdir("$output_dir/libs");
	}

	foreach($libs_to_export as $lib) {
		copy("$path_to_lib_dir/$lib", "$output_dir/libs/$lib");
	}


}

function fix_R_imports($dir) {
	if(is_file($dir)) return;
	array_map("fix_R", glob("$dir/*.java"));
	array_map(__FUNCTION__, glob("$dir/*"));
}

function fix_R($file) {
	if(is_dir($file)) return;
	file_put_contents($file, preg_replace("/import org.pocketcampus.plugin.[a-z]+.R;/", "import org.pocketcampus.R;", file_get_contents($file)));
}


// LOGIC

$output_dir = "../../android/PocketCampus";
$project_name = "PocketCampus";

if(!is_dir("$output_dir"))
	mkdir("$output_dir", 0755, true);

generate_android_manifest($output_dir, false);
generate_ant_properties($output_dir);
generate_build_xml($output_dir, "$project_name");
generate_proguard_cfg($output_dir);
//generate_project_properties($output_dir, false, array("../PocketCampusLib"));
generate_project_properties($output_dir, false, array("../google_play_services/libproject/google-play-services_lib"));
generate_dot_classpath($output_dir);
generate_dot_project($output_dir, "$project_name");
// need to manually generate local.properties;

delete_dir("$output_dir/src");
delete_dir("$output_dir/res");
collect_res("$output_dir");
collect_src("$output_dir");

delete_dir("$output_dir/libs");
export_libs("$output_dir");

fix_R_imports("$output_dir/src");

?>
