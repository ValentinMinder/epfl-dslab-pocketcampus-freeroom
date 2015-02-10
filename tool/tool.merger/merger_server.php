<?php
/**
  SERVER MERGER

  This script merges the PocketCampus java server
  It transforms all the plugins into a single project
  that you can build using ANT or import it in eclipse

  You should specify $plugins_to_merge and $libs_to_export

  @Author: Amer C (amer.chamseddine@epfl.ch)
*/

chdir(dirname(__FILE__));

$plugins_to_merge = array("Authentication", "Camipro", "Moodle", "Food", "Transport", "News", "Satellite", "Map", "Directory", "PushNotif", "Events", "IsAcademia", "CloudPrint", "RecommendedApps", "FreeRoom");

$libs_to_export = array( // keep this alphabetical
		"backport-util-concurrent-3.1.jar", "bcprov-jdk15-146.jar",
		"commons-codec-1.4.jar", "commons-io-2.0.1.jar", "commons-lang-2.6.jar", "commons-lang3-3.0.1.jar", "commons-logging-1.1.1.jar",
		"gcm-server.jar", "gson-1.7.1.jar", 
		"org.json-20120521.jar",
		"httpclient-4.1.2.jar", "httpcore-4.1.2.jar",
		"javapns_2.2.jar",
		"jetty-ajp-8.0.0.M3.jar", "jetty-annotations-8.0.0.M3.jar", "jetty-client-8.0.0.M3.jar", "jetty-continuation-8.0.0.M3.jar", "jetty-deploy-8.0.0.M3.jar", "jetty-http-8.0.0.M3.jar", "jetty-io-8.0.0.M3.jar", "jetty-jmx-8.0.0.M3.jar", "jetty-jndi-8.0.0.M3.jar", "jetty-overlay-deployer-8.0.0.M3.jar", "jetty-plus-8.0.0.M3.jar", "jetty-policy-8.0.0.M3.jar", "jetty-rewrite-8.0.0.M3.jar", "jetty-security-8.0.0.M3.jar", "jetty-server-8.0.0.M3.jar", "jetty-servlet-8.0.0.M3.jar", "jetty-servlets-8.0.0.M3.jar", "jetty-util-8.0.0.M3.jar", "jetty-webapp-8.0.0.M3.jar", "jetty-websocket-8.0.0.M3.jar", "jetty-xml-8.0.0.M3.jar",
		"joda-time-2.3.jar", "json_simple-1.1.jar", "jsoup-1.7.2.jar",
		"libthrift-0.9.2.jar", "log4j-1.2.16.jar",
		"mail.jar", "mysql-connector-java-5.1.15-bin.jar",
		"servlet-api-3.0.jar", "slf4j-api-1.6.2.jar", "slf4j-simple-1.6.2.jar",
		"tequila-client.jar",
		"unboundid-ldapsdk-se.jar");

if(!empty($include_tests)) {
	$libs_to_export[] = "junit-4.11.jar";
	$libs_to_export[] = "hamcrest-core-1.3.jar";
}

$path_to_plugin_dir = "../../plugin";
$path_to_platform_dir = "../../platform";
$path_to_lib_dir = "../../platform/platform.shared/lib";


// FUNCTIONS

function create_elem_w_attrib($doc, $tag, $attrib) {
	$elem = $doc->createElement($tag);
	foreach($attrib as $k => $v)
		$elem->setAttribute($k, $v);
	return $elem;
}

function generate_build_xml($output_dir, $project_name){
	global $libs_to_export;
	global $path_to_lib_dir;
	global $include_tests;

	$doc = new DOMDocument("1.0", "utf-8");
	$doc->formatOutput = true;

	$doc->appendChild($proj = create_elem_w_attrib($doc, "project", array("basedir" => ".", "name" => "$project_name", "default" => "build")));

	$proj->appendChild(create_elem_w_attrib($doc, "property", array("environment" => "env")));
	$proj->appendChild(create_elem_w_attrib($doc, "property", array("name" => "debuglevel", "value" => "source,lines,vars")));
	$proj->appendChild(create_elem_w_attrib($doc, "property", array("name" => "target", "value" => "1.7")));
	$proj->appendChild(create_elem_w_attrib($doc, "property", array("name" => "source", "value" => "1.7")));

	$proj->appendChild($path = create_elem_w_attrib($doc, "path", array("id" => "$project_name.classpath")));
	$path->appendChild(create_elem_w_attrib($doc, "pathelement", array("location" => "bin")));
	foreach($libs_to_export as $lib)
		$path->appendChild(create_elem_w_attrib($doc, "pathelement", array("location" => "lib/$lib")));

	$proj->appendChild($target = create_elem_w_attrib($doc, "target", array("name" => "init")));
	$target->appendChild(create_elem_w_attrib($doc, "mkdir", array("dir" => "bin")));
	$target->appendChild($copy = create_elem_w_attrib($doc, "copy", array("includeemptydirs" => "false", "todir" => "bin")));
	$copy->appendChild($fileset = create_elem_w_attrib($doc, "fileset", array("dir" => "src")));
	$fileset->appendChild(create_elem_w_attrib($doc, "exclude", array("name" => "**/*.java")));

	$proj->appendChild($target = create_elem_w_attrib($doc, "target", array("name" => "clean")));
	$target->appendChild(create_elem_w_attrib($doc, "delete", array("dir" => "bin")));
	$target->appendChild(create_elem_w_attrib($doc, "delete", array("dir" => "report")));
	$target->appendChild(create_elem_w_attrib($doc, "delete", array("file" => "$project_name.jar")));

	$proj->appendChild(create_elem_w_attrib($doc, "target", array("depends" => "clean", "name" => "cleanall")));
	$proj->appendChild(create_elem_w_attrib($doc, "target", array("depends" => "build-subprojects,build-project", "name" => "build")));
	$proj->appendChild(create_elem_w_attrib($doc, "target", array("name" => "build-subprojects")));

	$proj->appendChild($target = create_elem_w_attrib($doc, "target", array("depends" => "init", "name" => "build-project")));
	$target->appendChild(create_elem_w_attrib($doc, "echo", array("message" => "\${ant.project.name}: \${ant.file}")));
	$target->appendChild($javac = create_elem_w_attrib($doc, "javac", array("includeantruntime" => "false", "debug" => "true", "encoding" => "UTF-8", "debuglevel" => "\${debuglevel}", "destdir" => "bin", "source" => "\${source}", "target" => "\${target}")));
	$javac->appendChild(create_elem_w_attrib($doc, "src", array("path" => "src")));
	$javac->appendChild(create_elem_w_attrib($doc, "classpath", array("refid" => "$project_name.classpath")));

	$proj->appendChild(create_elem_w_attrib($doc, "target", array("description" => "Build all projects which reference this project. Useful to propagate changes.", "name" => "build-refprojects")));

	$proj->appendChild($target = create_elem_w_attrib($doc, "target", array("name" => "$project_name")));
	$target->appendChild($java = create_elem_w_attrib($doc, "java", array("classname" => "org.pocketcampus.platform.server.launcher.ServerLauncher", "failonerror" => "true", "fork" => "yes")));
	$java->appendChild(create_elem_w_attrib($doc, "classpath", array("refid" => "$project_name.classpath")));

	if(empty($include_tests)) {
		$proj->appendChild($target = create_elem_w_attrib($doc, "target", array("depends" => "build", "name" => "create_run_jar")));
		$target->appendChild($jar = create_elem_w_attrib($doc, "jar", array("destfile" => "$project_name.jar", "filesetmanifest" => "mergewithoutmain")));
		$jar->appendChild($manifest = create_elem_w_attrib($doc, "manifest", array()));
		$manifest->appendChild(create_elem_w_attrib($doc, "attribute", array("name" => "Main-Class", "value" => "org.pocketcampus.platform.server.launcher.ServerLauncher")));
		$manifest->appendChild(create_elem_w_attrib($doc, "attribute", array("name" => "Class-Path", "value" => ".")));
		$jar->appendChild(create_elem_w_attrib($doc, "fileset", array("dir" => "bin")));
		foreach($libs_to_export as $lib)
			$jar->appendChild(create_elem_w_attrib($doc, "zipfileset", array("excludes" => "META-INF/*.SF", "src" => "lib/$lib")));
	} else {
		$proj->appendChild($target = create_elem_w_attrib($doc, "target", array("depends" => "build", "name" => "junit")));
		$target->appendChild(create_elem_w_attrib($doc, "mkdir", array("dir" => "report")));
		$target->appendChild($junit = create_elem_w_attrib($doc, "junit", array("printsummary" => "on", "fork" => "true", "haltonfailure" => "no", "showoutput" => "yes")));
		$junit->appendChild($classpath = create_elem_w_attrib($doc, "classpath", array()));
		$classpath->appendChild(create_elem_w_attrib($doc, "path", array("refid" => "$project_name.classpath")));
		$classpath->appendChild(create_elem_w_attrib($doc, "path", array("location" => "bin")));
		$junit->appendChild(create_elem_w_attrib($doc, "formatter", array("type" => "xml")));
		$junit->appendChild($batchtest = create_elem_w_attrib($doc, "batchtest", array("todir" => "report")));
		$batchtest->appendChild(create_elem_w_attrib($doc, "fileset", array("dir" => "src", "includes" => "**/*Test*.java")));
	}

	file_put_contents("$output_dir/build.xml", $doc->saveXML());
}

function generate_dot_classpath($output_dir){
	global $libs_to_export;
	global $path_to_lib_dir;

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
	$cpe->setAttribute("kind", "con");
	$cpe->setAttribute("path", "org.eclipse.jdt.launching.JRE_CONTAINER");

	foreach($libs_to_export as $lib) {
		$cpe = $doc->createElement("classpathentry");
		$cp->appendChild($cpe);
		$cpe->setAttribute("kind", "lib");
		$cpe->setAttribute("path", "lib/$lib");
	}

	$cpe = $doc->createElement("classpathentry");
	$cp->appendChild($cpe);
	$cpe->setAttribute("kind", "output");
	$cpe->setAttribute("path", "bin");

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
	$name->appendChild($doc->createTextNode("org.eclipse.jdt.core.javabuilder"));
	$buildCommand->appendChild($name);
	$arguments = $doc->createElement("arguments");
	$arguments->appendChild($doc->createTextNode(""));
	$buildCommand->appendChild($arguments);

	$natures = $doc->createElement("natures");
	$projectDescription->appendChild($natures);

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
		// echo "$source -> $dest\n\n";
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

function collect_src($output_dir) {
	global $plugins_to_merge;
	global $path_to_plugin_dir;
	global $path_to_platform_dir;
	global $include_tests;

	foreach($plugins_to_merge as $plgn) {
		$plugin = strtolower($plgn);
		if(is_dir("$path_to_plugin_dir/$plugin/plugin.$plugin.server/src")) // if has .server proj
			copyr("$path_to_plugin_dir/$plugin/plugin.$plugin.server/src", "$output_dir/src");
		if(is_dir("$path_to_plugin_dir/$plugin/plugin.$plugin.shared/src")) // if has .shared proj
			copyr("$path_to_plugin_dir/$plugin/plugin.$plugin.shared/src", "$output_dir/src");
		if(!empty($include_tests))
			if(is_dir("$path_to_plugin_dir/$plugin/plugin.$plugin.server.tests/src"))
				copyr("$path_to_plugin_dir/$plugin/plugin.$plugin.server.tests/src", "$output_dir/src");
	}

	copyr("$path_to_platform_dir/platform.server/src", "$output_dir/src");
	copyr("$path_to_platform_dir/platform.shared/src", "$output_dir/src");
	if(!empty($include_tests))
		copyr("$path_to_platform_dir/platform.server.tests/src", "$output_dir/src");

}

function export_libs($output_dir) {
	global $libs_to_export;
	global $path_to_lib_dir;

	if(!is_dir("$output_dir/lib")) {
		mkdir("$output_dir/lib");
	}

	foreach($libs_to_export as $lib) {
		copy("$path_to_lib_dir/$lib", "$output_dir/lib/$lib");
	}


}


// LOGIC

if(empty($include_tests)) {
	$output_dir = "../../server/PocketCampusServer";
	$project_name = "PocketCampusServer";
} else {
	$output_dir = "../../server/PocketCampusServerTests";
	$project_name = "PocketCampusServerTests";
}

if(!is_dir("$output_dir"))
	mkdir("$output_dir", 0755, true);

generate_build_xml($output_dir, "$project_name");
generate_dot_classpath($output_dir);
generate_dot_project($output_dir, "$project_name");

delete_dir("$output_dir/src");
collect_src("$output_dir");

delete_dir("$output_dir/lib");
export_libs("$output_dir");



?>
