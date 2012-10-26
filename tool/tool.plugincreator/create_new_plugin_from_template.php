<?php
/**
  PLUGIN CREATOR
  
  This script creates a new blank plugin from a template
  It copies all the files from the blank's plugin directory
  and renames everything
  
  You should specify $new_plugin
  
  @Author: Amer C (amer.chamseddine@epfl.ch)
*/

chdir(dirname(__FILE__));

$template_plugin = "Blank";
$new_plugin = "MyPlugin";

$deleted_dirs = array();
$emptied_dirs = array();
$processed_dirs = array();
$processed_files = array();
$weird_cases = array();

$replacements = array();

$to_delete_dirs = array(".svn");
$to_empty_dirs = array("bin", "gen");

function process_dir($dir) {

	global $replacements;
	
	global $deleted_dirs;
	global $emptied_dirs;
	global $processed_dirs;
	global $processed_files;
	global $weird_cases;
	
	global $to_delete_dirs;
	global $to_empty_dirs;
	
	if ($handle = opendir($dir)) {
		while (false !== ($entry = readdir($handle))) {
			if($entry != "." && $entry != "..") {
			
				//echo "$dir/$entry\n";
				if(is_file("$dir/$entry")) {
					$m_entry = $entry;
					foreach($replacements as $k => $v) {
						$m_entry = implode($v, explode($k, $m_entry));
					}
					if($entry != $m_entry)
						system("mv $dir/$entry $dir/$m_entry");
					process_file("$dir/$m_entry");
					$processed_files[] = "$dir/$m_entry";
				} elseif(is_dir("$dir/$entry")) {
					if(in_array($entry, $to_delete_dirs)) {
						system("rm -Rf $dir/$entry");
						$deleted_dirs[] = "$dir/$entry";
					} elseif(in_array($entry, $to_empty_dirs)) {
						system("rm -Rf $dir/$entry");
						system("mkdir $dir/$entry");
						$emptied_dirs[] = "$dir/$entry";
					} else {
						$m_entry = $entry;
						foreach($replacements as $k => $v) {
							$m_entry = implode($v, explode($k, $m_entry));
						}
						if($entry != $m_entry)
							system("mv $dir/$entry $dir/$m_entry");
						process_dir("$dir/$m_entry");
						$processed_dirs[] = "$dir/$m_entry";
					}
				} else {
					$weird_cases[] = "$dir/$entry";
				}
			
			}
		}
		closedir($handle);
	}

}

function process_file($file) {
	global $replacements;
	$contents = file_get_contents($file);
	foreach($replacements as $k => $v) {
		$contents = implode($v, explode($k, $contents));
	}
	file_put_contents($file, $contents);
}

function augment_replacements() {
	global $replacements;
	global $template_plugin;
	global $new_plugin;
	$replacements[strtolower($template_plugin)] = strtolower($new_plugin);
	$replacements[$template_plugin] = $new_plugin;
	$replacements[strtoupper($template_plugin)] = strtoupper($new_plugin);
}


// ================ LOGIC ================ \\

$path_to_plugins_dir = "../../plugin";
$template_plugin_dir = "$path_to_plugins_dir/" . strtolower($template_plugin);
$new_plugin_dir = "$path_to_plugins_dir/" . strtolower($new_plugin);

if(is_dir($new_plugin_dir) || is_file($new_plugin_dir)) {
	echo "ERROR `$new_plugin_dir` already exists\n";
	exit;
}

system("cp -R $template_plugin_dir $new_plugin_dir");

augment_replacements();
process_dir($new_plugin_dir);

echo "================= deleted_dirs ======================\n";
print_r($deleted_dirs);
echo "================= emptied_dirs ======================\n";
print_r($emptied_dirs);
echo "================= processed_dirs ======================\n";
print_r($processed_dirs);
echo "================= processed_files ======================\n";
print_r($processed_files);
echo "================= weird_cases ======================\n";
print_r($weird_cases);

?>
