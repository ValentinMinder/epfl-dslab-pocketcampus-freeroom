import shutil, errno, sys, os, re

class Utils():

	@staticmethod
	def copyanything(src, dst):
		try:
			shutil.copytree(src, dst)
		except OSError as exc: # python >2.5

			if exc.errno == errno.ENOTDIR:
				shutil.copy(src, dst)
			else: raise

	@staticmethod
	def walk_and_replace(folder, replacements_map):
		for dirname, dirnames, filenames in os.walk(folder):
			for subdirname in dirnames:
				folder_path = os.path.join(dirname, subdirname)
				for haystack, replacement in replacements_map.items():
					Utils.rename_file(folder_path, haystack, replacement)
			for filename in filenames:
				file_path = os.path.join(dirname, filename)
				for haystack, replacement in replacements_map.items():
					Utils.replace_file_content(file_path, haystack, replacement)
					Utils.rename_file(file_path, haystack, replacement)
					
				
		
	@staticmethod
	def rename_file(file_path, haystack, replacement):
		file_name = os.path.basename(file_path)
		file_name_renamed = file_name.replace(haystack, replacement)
		file_path_renamed = os.path.join(os.path.dirname(file_path), file_name_renamed)
		if file_path != file_path_renamed:
			#print(">> Renaming file '"+file_name+"' -> '"+file_name_renamed+"'")
			os.rename(file_path, file_path_renamed)
		
	@staticmethod
	def replace_file_content(file_path, haystack, replacement):
		newlines = []
		with open(file_path,'r') as f:
			for line in f.readlines():
				new_line = line.replace(haystack, replacement)
				newlines.append(line.replace(haystack, replacement))
				#if new_line != line:
					#print(">> Replacing line in "+os.path.basename(file_path))
		with open(file_path, 'w') as f:
			for line in newlines:
				f.write(line)
			f.close()


PLUGIN_TEMPLATE_FOLDER_PATH = 'PluginTemplate'
PLUGIN_ID_HAYSTACK = '__PluginID__'
PLUGIN_ID_LOW_HAYSTACK = '__PluginID_low__'

#
# Checking arguments
#
if not re.match("^.*ios/iphone/Tools/PluginCreator$", os.getcwd()):
	print("\nError : please cd into <PocketCampusRepositoryRoot>/ios/iphone/Tools/PluginCreator\n")
	exit(-1)

if len(sys.argv) < 2:
	print("\nUsage: python "+sys.argv[0]+" <PluginName> [<path_to_PocketCampus_Xcode_project>]\n")
	exit(-1)

xcode_project_path = "../../PocketCampus"
if (len(sys.argv) > 2):
	xcode_project_path = sys.argv[2]
pbxproj_path = os.path.join(xcode_project_path, "PocketCampus.xcodeproj", "project.pbxproj")

if not os.path.isfile(pbxproj_path):
	printf("Error: invalid Xcode project path or unsupported version of Xcode.")
	exit(-1)

plugin_identifier = sys.argv[1]

#
# Checking plugin name
#

if " " in plugin_identifier:
	print("\nError : bad plugin name. Cannot contain blanks.")
	exit(-1)
	
if not plugin_identifier.istitle():
	while True:
		choice = raw_input('\nError : bad plugin name. Should be titlecased (satisfy python str.istitle()). Continue anyway ? (y/N) [N]: ')
		if choice == 'y':
			break
		if choice == 'N' or choice == '':
			exit(0)

plugin_identifier_low = plugin_identifier.lower()

#
# Checking project path
#

try:
   with open(pbxproj_path) as f: pass
except IOError as e:
   print("\nError: wrong <path_to_PocketCampus_Xcode_project>. Cannot find "+pbxproj_path)
   exit(-1)
   
  
#
# Duplicate plugin template folder
#


plugin_folder_path = plugin_identifier+'Plugin'

plugin_folder_name = plugin_folder_path

if os.path.isfile(plugin_folder_path):
   print("\nError: plugin folder '"+plugin_folder_path+"' already exists. Please delete it first.")
   exit(-1)

try:
	Utils.copyanything(PLUGIN_TEMPLATE_FOLDER_PATH, plugin_folder_path)
except Exception as exc:
	print("Error: could not create plugin folder. ")
	exit(-1)

#
# Renaming and replacing files and folders names/content
#

Utils.walk_and_replace(plugin_folder_path, {PLUGIN_ID_HAYSTACK:plugin_identifier, PLUGIN_ID_LOW_HAYSTACK:plugin_identifier_low})

#
# Copy valid plugin folder in PocketCampus project
#
plugin_folder_path_within_PC = os.path.join(xcode_project_path, 'Plugins', plugin_folder_path)
try:
	Utils.copyanything(plugin_folder_path, plugin_folder_path_within_PC)
	shutil.rmtree(plugin_folder_path)
except Exception as exs:
	printf("Error: could not copy plugin folder into PocketCampus project")
	exit(-1)

print("--> Plugin base created in: "+plugin_folder_path_within_PC)	
print("--> You can now add Thrift generated files in: "+plugin_folder_path_within_PC+"/Model/ThriftTypes+Services/\nThis can done automatically by runnig the Thrift compiler script.")