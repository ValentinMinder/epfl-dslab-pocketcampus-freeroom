<?php
/**
  THRIFT COMPILER
  
  This script compiles the thrift definition files of a plugin
  It places the generated files in the appropriate directories
  as per PocketCampus directory structure
  
  Usage: $0 plugin=<...> platform=<ios|android>
  (Replace <...> by your plugin name)
  You can use plugin=platform to compile the thrift files of the platform.shared project
  
  @Author: Amer C (amer.chamseddine@epfl.ch)
*/

chdir(dirname(__FILE__));

// Parsing arguments
$args_default = [
  'plugin' => null,
  'platform' => null
];
// Puts CLI args into $args, with default values if necessary
parse_str(implode('&', array_slice($argv, 1)), $args);
$args = array_merge($args_default, $args);

$plugin_name = $args['plugin'];
$platform = $args['platform'];

if ($plugin_name == null || $platform == null) {
	echo "Usage: $0 plugin=<...> platform=<ios|android>\n";
	exit(1);
}

// LOGIC: DONT TOUCH THE CODE BELOW

$thrift_bin = "binaries/thrift-linux-0.9.2";
if (strtoupper(substr(PHP_OS, 0, 3)) === 'WIN') {
	$cwd = getcwd();
	$thrift_bin = "$cwd/binaries/thrift-win-0.9.2.exe";
} elseif (strtoupper(substr(PHP_OS, 0, 6)) === 'DARWIN') {
	$thrift_bin = "binaries/thrift-mac-0.9.2";
}
$plugin_ios_dir = "../../ios/PocketCampus/Plugins/{$plugin_name}Plugin/Model/ThriftTypes+Services";
$plugin_name = strtolower($plugin_name);
$string_plugin = "plugin";
$plugin_shared_dir = "../../$string_plugin/$plugin_name/$string_plugin.$plugin_name.shared";
if($plugin_name == "platform") {
	$string_plugin = "platform";
        $plugin_shared_dir = "../../$string_plugin/$string_plugin.shared";
	$plugin_ios_dir = "../../ios/PocketCampus/PocketCampus/Model/SharedThriftTypes+Services";
}

foreach(glob("$plugin_shared_dir/def/*.thrift") as $def_file) {
	echo "Compiling $def_file for platform $platform\n";
	// TODO read package name from thrift file and clear corresponding directory
	if ($platform === "ios") {
		system("$thrift_bin --gen cocoa:validate_required -out $plugin_ios_dir $def_file");
	} else if ($platform === "android") {
		system("$thrift_bin --gen java:hashcode,private-members -out $plugin_shared_dir/src $def_file");
	} else {
		echo "ERROR: Platform $platform is incorrect\n";
	}
}

?>