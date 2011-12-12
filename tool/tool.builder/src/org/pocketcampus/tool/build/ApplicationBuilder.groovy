package org.pocketcampus.tool.build

import org.pocketcampus.tool.build.parser.ClasspathEntry;
import org.pocketcampus.tool.build.parser.DotClasspath;
import org.pocketcampus.tool.build.template.AndroidPomTemplate;
import org.pocketcampus.tool.build.template.SharedPomTemplate;
import org.pocketcampus.tool.build.template.MergerBuilderTemplate;
import org.pocketcampus.tool.build.template.SharedDotClasspathTemplate
import org.pocketcampus.tool.build.template.ManifestTemplate;
import org.pocketcampus.tool.build.template.ProguardTemplate;
import org.pocketcampus.tool.build.template.ProjectDotProperties;
import org.pocketcampus.tool.build.utils.FileUtils;

class ApplicationBuilder {
	def final public static APPLICATION_NAME = "merged"
	def final public static APPLICATION_NAME_ANDROID = APPLICATION_NAME + ".android"
	def final public static APPLICATION_NAME_SHARED = APPLICATION_NAME + ".shared"
	
	def final private static TARGET_DIRECTORY = "../../merged/";
	def final private static TARGET_DIRECTORY_ANDROID = TARGET_DIRECTORY + "android/";
	def final private static TARGET_DIRECTORY_SHARED = TARGET_DIRECTORY + "shared/";
	def final private static TARGET_DIRECTORY_SERVER = TARGET_DIRECTORY + "server/";

	def static plugins = new ArrayList();

	public static void main(String[] args) {
		print "Listing plugins... "
		new File('../../plugin').eachFile {
			if(!it.getName().startsWith(".")) {
				// skip the hidden directories directories such as ".svn"
				Plugin plugin = Plugin.fromDirectory(it)
				plugins.add(plugin);
			}
		}

		if(plugins.size() == 0) {
			println "none found."
			return;
		}

		println plugins.size() + " found."
		
		println "Cleaning up directories..."
		new File(TARGET_DIRECTORY_ANDROID).deleteDir()
		new File(TARGET_DIRECTORY_SHARED).deleteDir()
		new File(TARGET_DIRECTORY_SERVER).deleteDir()

		println ""
		
		String manifestBuffer = "";
		ArrayList classpathEntriesBuffer = new ArrayList()
		int skippedPluginsCount = 0

		for (Plugin plugin : plugins) {
			println "=> " + plugin.mName.capitalize()

			try {
				plugin.parse()
				manifestBuffer += plugin.getManifest().getText()
				classpathEntriesBuffer.addAll(plugin.getDotClasspath().getClasspathEntries())
				
				plugin.copyTo(TARGET_DIRECTORY_ANDROID, TARGET_DIRECTORY_SHARED, TARGET_DIRECTORY_SERVER)
				
				//println "Done."

			} catch (IllegalArgumentException e) {
				println e
				println "Skipping plugin: " + plugin.mName.capitalize()
				skippedPluginsCount++
			}

			println ""
		}

		println "Making Android Manifest"
		String finalManifest = ManifestTemplate.getText(manifestBuffer);
		new File(TARGET_DIRECTORY_ANDROID + "AndroidManifest.xml").write(finalManifest);
		
		println "Making Android Proguard configuration"
		String proguardConf = ProguardTemplate.getText();
		new File(TARGET_DIRECTORY_ANDROID + "proguard.cfg").write(proguardConf);
		
		println "Making Android Project Properties"
		new File(TARGET_DIRECTORY_ANDROID + "project.properties").write(ProjectDotProperties.getText());
		
//		println "Making Android Project Builder List"
//		new File(TARGET_DIRECTORY_ANDROID + ".externalToolBuilders/PocketCampus Merger.launch").write(MergerBuilderTemplate.getText());
		
		print "Making Android Classpath "
		String finalClasspath = DotClasspath.fromEntries(classpathEntriesBuffer).getText()
		println "(" + DotClasspath.fromEntries(classpathEntriesBuffer).getClasspathEntries().size() + " imports)"
		new File(TARGET_DIRECTORY_ANDROID + ".classpath").write(finalClasspath);
		
		println "Making Android pom.xml"
		String androidPom = AndroidPomTemplate.getText()
		new File(TARGET_DIRECTORY_ANDROID + "pom.xml").write(androidPom);
		
		println "Making Shared pom.xml"
		String sharedPom = SharedPomTemplate.getText()
		new File(TARGET_DIRECTORY_SHARED + "pom.xml").write(sharedPom);
		
		println "Making Shared Classpath "
		new File(TARGET_DIRECTORY_SHARED + ".classpath").write(SharedDotClasspathTemplate.getText());
		
		if(skippedPluginsCount != 0) {
			println ""
			println "Warning: Skipped " + skippedPluginsCount + " plugins. Check the log for more info"
		}
		
		println ""
		println "Merged " + (plugins.size() - skippedPluginsCount) + " plugins in " + new File(TARGET_DIRECTORY).getCanonicalPath()
	}

}

















