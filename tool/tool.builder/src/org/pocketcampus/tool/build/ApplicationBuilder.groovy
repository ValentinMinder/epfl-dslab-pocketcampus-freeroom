package org.pocketcampus.tool.build

import org.apache.ivy.util.FileUtil;
import org.pocketcampus.tool.build.template.ManifestTemplate;

class ApplicationBuilder {
	def final public static APPLICATION_NAME = "PocketCampus"
	def final public static APPLICATION_NAME_ANDROID = APPLICATION_NAME + " (android)"
	def final public static APPLICATION_NAME_SHARED = APPLICATION_NAME + " (shared)"
	
	def final private static TARGET_DIRECTORY = "../../target/";
	def final private static TARGET_DIRECTORY_ANDROID = TARGET_DIRECTORY + "android/";
	def final private static TARGET_DIRECTORY_SHARED = TARGET_DIRECTORY + "shared/";
	def final private static TARGET_DIRECTORY_SERVER = TARGET_DIRECTORY + "server/";

	def static plugins = new ArrayList();

	public static void main(String[] args) {
		println "=== SCANNING FOR PLUGINS ==="
		new File('../../plugin').eachFile {
			Plugin plugin = Plugin.fromDirectory(it)
			plugins.add(plugin);
		}

		if(plugins.size() == 0) {
			println "None found."
			return;
		}

		println plugins.size() + " found."
		println ""

		println "=== MERGING PLUGINS ==="
		String manifestBuffer = "";
		ArrayList usedPackages = new ArrayList()

		for (Plugin plugin : plugins) {
			println "=> " + plugin.mName.capitalize()

			try {
				plugin.parse()
				plugin.copyTo(TARGET_DIRECTORY_ANDROID, TARGET_DIRECTORY_SHARED, TARGET_DIRECTORY_SERVER)
				
				manifestBuffer += plugin.mManifest.text
				//println "Done."

			} catch (IllegalArgumentException e) {
				println e
			}

			println ""
		}

		println "=== CREATING APPLICATION ==="
		println "=> Manifest"
		String finalManifest = ManifestTemplate.getText(manifestBuffer);
		new File(TARGET_DIRECTORY_ANDROID + "AndroidManifest.xml").write(finalManifest);
	}

}

















