package org.pocketcampus.tool.build

import org.pocketcampus.tool.build.parser.ClasspathEntry;
import org.pocketcampus.tool.build.parser.DotClasspath;
import org.pocketcampus.tool.build.template.AndroidPomTemplate;
import org.pocketcampus.tool.build.template.SharedPomTemplate;
import org.pocketcampus.tool.build.template.SharedDotClasspathTemplate
import org.pocketcampus.tool.build.template.ManifestTemplate;
import org.pocketcampus.tool.build.template.ProguardTemplate;
import org.pocketcampus.tool.build.template.ProjectDotProperties;

class ApplicationBuilder {
	def final public static APPLICATION_NAME = "pocketcampus"
	def final public static APPLICATION_NAME_ANDROID = APPLICATION_NAME + "-android"
	def final public static APPLICATION_NAME_SHARED = APPLICATION_NAME + "-shared"
	
	def final private static TARGET_DIRECTORY = "../../merged/";
	def final private static TARGET_DIRECTORY_ANDROID = TARGET_DIRECTORY + "android/";
	def final private static TARGET_DIRECTORY_SHARED = TARGET_DIRECTORY + "shared/";
	def final private static TARGET_DIRECTORY_SERVER = TARGET_DIRECTORY + "server/";

	def static plugins = new ArrayList();

	public static void main(String[] args) {
		print "Listing plugins... "
		new File('../../plugin').eachFile {
			Plugin plugin = Plugin.fromDirectory(it)
			plugins.add(plugin);
		}

		if(plugins.size() == 0) {
			println "none found."
			return;
		}

		println plugins.size() + " found."
		
		println "Cleaning directories..."
		new File(TARGET_DIRECTORY_ANDROID).delete();
		new File(TARGET_DIRECTORY_SHARED).delete();
		new File(TARGET_DIRECTORY_SERVER).delete();
		
		println "=== MERGING APPLICATION ==="
		String manifestBuffer = "";
		ArrayList classpathEntriesBuffer = new ArrayList()

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
			}

			println ""
		}

		println "=== COMPLETING APPLICATION ==="
		println "=> Android: Manifest"
		String finalManifest = ManifestTemplate.getText(manifestBuffer);
		new File(TARGET_DIRECTORY_ANDROID + "AndroidManifest.xml").write(finalManifest);
		
		println "=> Android: Proguard configuration"
		String proguardConf = ProguardTemplate.getText();
		new File(TARGET_DIRECTORY_ANDROID + "proguard.cfg").write(proguardConf);
		
		println "=> Android: Project Properties"
		new File(TARGET_DIRECTORY_ANDROID + "project.properties").write(ProjectDotProperties.getText());
		
		print "=> Android: Classpath"
		String finalClasspath = DotClasspath.fromEntries(classpathEntriesBuffer).getText()
		println "(" + DotClasspath.fromEntries(classpathEntriesBuffer).getClasspathEntries().size() + " imports)"
		new File(TARGET_DIRECTORY_ANDROID + ".classpath").write(finalClasspath);
		
		println "=> Android: pom.xml"
		String androidPom = AndroidPomTemplate.getText()
		new File(TARGET_DIRECTORY_ANDROID + "pom.xml").write(androidPom);
		
		println "=> Shared: pom.xml"
		String sharedPom = SharedPomTemplate.getText()
		new File(TARGET_DIRECTORY_SHARED + "pom.xml").write(sharedPom);
		
		println "=> Shared: Classpath "
		new File(TARGET_DIRECTORY_SHARED + ".classpath").write(SharedDotClasspathTemplate.getText());
	}

}

















