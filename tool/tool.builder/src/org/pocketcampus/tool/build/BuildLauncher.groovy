package org.pocketcampus.tool.build

class BuildLauncher {
	def static LAUNCHER_MANIFEST_PATH = '../platform.launcher.android/AndroidManifest.xml'
	
	// insertion points in the templates
	def static MANIFEST_MARKER = "[PLUGIN_DECLARATIONS]";
	
	public static void main(String[] args) {
		def pluginManifests = ""
		
		// parses the files of each plugin
		new File('../platform.launcher.android.bak').eachFile {
			switch(it.getName()) {
				case 'AndroidManifest.xml':
					pluginManifests += parseManifest(it);
					break;
			}
		}
		
		// fills in the Manifest template
		def mergedManifest = ManifestTemplate.getText(pluginManifests);
		
		// overwrites the launcher files
		new File(LAUNCHER_MANIFEST_PATH).write(mergedManifest);
		
		print 'Done.'
	}

	public static String parseManifest(File manifest) {
		// reads Manifest
		def ns = new groovy.xml.Namespace('http://schemas.android.com/apk/res/android', 'ns')
		def root = new groovy.util.XmlParser().parse(manifest)
		def pkg = root.attribute('package')
		def app = root.application
		
		// makes names fully qualified
		def makeNameFullyQualified = {
			def name = it.attribute(ns.name)
			if(name.charAt(0) != ".") return
			it.attributes()[ns.name] = pkg + name
		}
		
		// generates code for this plugin
		def writer = new StringWriter()
		def printer = new XmlNodePrinter(new PrintWriter(writer))
		def print = { printer.print(it) }
		
		// applies and print
		app.activity.each(makeNameFullyQualified).each(print)
		app.service.each(makeNameFullyQualified).each(print)
		app.receiver.each(makeNameFullyQualified).each(print)
		
		def pluginManifest = writer.toString()
		pluginManifest = '\n/* FROM PLUGIN ' + pkg + " */\n" + pluginManifest
		return pluginManifest
	}
}












