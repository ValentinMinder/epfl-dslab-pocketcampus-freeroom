<?php

chdir(dirname(__FILE__));

// ARGUMENTS: SET THE PLUGIN NAME HERE (set to "sdk" to compile common thrift definition files)

$plugin_name = "moodle";

// LOGIC: DONT TOUCH THE CODE BELOW

$thrift_bin = "binaries/thrift-linux-0.8.0-dev";
if (strtoupper(substr(PHP_OS, 0, 3)) === 'WIN') {
	$thrift_bin = "binaries/thrift-win-0.7.0.exe";
} elseif (strtoupper(substr(PHP_OS, 0, 6)) === 'DARWIN') {
	$thrift_bin = "binaries/thrift-mac-0.8.0";
}
$string_plugin = "plugin";
if($plugin_name == "sdk") {
	$string_plugin = "platform";
}
$plugin_shared_dir = "../../$string_plugin/$plugin_name/$string_plugin.$plugin_name.shared";
$plugin_ios_dir = "../../ios/iphone/PocketCampus/Plugins/" . ucfirst($plugin_name) . "Plugin/Model/ThriftTypes+Services";

foreach(glob("$plugin_shared_dir/def/*.thrift") as $def_file) {
	//echo "Compiling $def_file\n";
	//echo "$thrift_bin -v --gen java:hashcode -out $plugin_shared_dir/src $def_file\n";
	system("$thrift_bin --gen java:hashcode -out $plugin_shared_dir/src $def_file");
	system("$thrift_bin --gen cocoa -out $plugin_ios_dir $def_file");
}

?>