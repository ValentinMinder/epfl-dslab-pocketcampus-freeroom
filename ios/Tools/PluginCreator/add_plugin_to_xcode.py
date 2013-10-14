import shutil, errno, sys, os, re

from mod_pbxproj import XcodeProject

if not re.match("^.*ios/iphone/Tools/PluginCreator$", os.getcwd()):
	print("\nError : please cd into <PocketCampusRepositoryRoot>/ios/iphone/Tools/PluginCreator\n")
	exit(-1)
	
if len(sys.argv) < 2:
	print("\nUsage: python "+sys.argv[0]+" <path_to_plugin_folder_in_PocketCampus_project> [<path_to_PocketCampus_Xcode_project>]\n")
	exit(-1)

plugin_folder_path = os.path.abspath(sys.argv[1])

if not os.path.exists(plugin_folder_path):
	print("Error: invalid plugin folder path '"+plugin_folder_path+"'")
	exit(-1)
	

xcode_project_path = "../../PocketCampus"

if len(sys.argv) == 3:
	xcode_project_path = sys.argv[2]

if not os.path.exists(xcode_project_path):
	print("Error: could not find PocketCampus Xcode project.")
	exit(-1)	

xcode_project_path = os.path.abspath(xcode_project_path)

pbxproj_path = os.path.join(xcode_project_path, "PocketCampus.xcodeproj", "project.pbxproj")

#
# Add plugin to Xcode project
#

plugin_folder_name = os.path.basename(plugin_folder_path)

project = XcodeProject.Load(pbxproj_path)

plugins_group = project.get_or_create_group('Plugins')

#Do not add lproj folders because they would not be reconized as localized files
project.add_folder(plugin_folder_path, parent=plugins_group, excludes=["^.*\.lproj$", "^.*\.strings$"])

plugin_group = project.get_or_create_group(plugin_folder_name, parent=plugins_group)

supporting_files_group = project.get_or_create_group('Supporting Files', parent=plugin_group)

strings_files = []
for dirname, dirnames, filenames in os.walk(plugin_folder_path):
	for filename in filenames:
		if re.match("^.*\.strings$", filename):
			project.add_file(os.path.join(plugin_folder_path, filename), parent=supporting_files_group)


project.save()

print("--> Plugin '"+plugin_folder_name+"' successfully added to Xcode project in Plugins group.")