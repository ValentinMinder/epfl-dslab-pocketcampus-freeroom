<?php
/**
  THRIFT COMPILER
  
  This script compiles the thrift definition files of a plugin
  It places the generated files in the appropriate directories
  as per PocketCampus directory structure
  
  You should specify $plugin_name
  You can set it to "sdk" to compile the thrift files of the SDK
  
  @Author: Amer C (amer.chamseddine@epfl.ch)
*/

chdir(dirname(__FILE__));

// ARGUMENTS: SET THE PLUGIN NAME HERE (set to "sdk" to compile common thrift definition files)

$plugin_name = "Authentication";

// LOGIC: DONT TOUCH THE CODE BELOW

$thrift_bin = "binaries/thrift-linux-0.7.0";
if (strtoupper(substr(PHP_OS, 0, 3)) === 'WIN') {
	$cwd = getcwd();
	$thrift_bin = "$cwd/binaries/thrift-win-0.7.0.exe";
} elseif (strtoupper(substr(PHP_OS, 0, 6)) === 'DARWIN') {
	$thrift_bin = "binaries/thrift-mac-0.7.0";
}
$plugin_ios_dir = "../../ios/PocketCampus/Plugins/{$plugin_name}Plugin/Model/ThriftTypes+Services";
$plugin_name = strtolower($plugin_name);
$string_plugin = "plugin";
if($plugin_name == "sdk") {
	$string_plugin = "platform";
	$plugin_ios_dir = "../../ios/PocketCampus/PocketCampus/Model/SharedThriftTypes+Services";
}
$plugin_shared_dir = "../../$string_plugin/$plugin_name/$string_plugin.$plugin_name.shared";

foreach(glob("$plugin_shared_dir/def/*.thrift") as $def_file) {
	echo "Compiling $def_file\n";
	// TODO read package name from thrift file and clear corresponding directory
	system("$thrift_bin --gen java:hashcode,private-members -out $plugin_shared_dir/src $def_file");
	system("$thrift_bin --gen cocoa -out $plugin_ios_dir $def_file");
}

?>
