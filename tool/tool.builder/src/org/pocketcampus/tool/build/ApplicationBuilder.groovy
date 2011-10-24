package org.pocketcampus.tool.build

class ApplicationBuilder {
	def final static TARGET_DIRECTORY = "../../target/";
	
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
			println "=> " + plugin.name.capitalize()
			
			try {
				plugin.parse()
				manifestBuffer += plugin.manifest.text
				//println "Done."
				
			} catch (IllegalArgumentException e) {
				println e
			}
			
			println ""
		}
		
		println "=== CREATING APPLICATION ==="
		println "=> Manifest"
		String finalManifest = ManifestTemplate.getText(manifestBuffer);
		new File(TARGET_DIRECTORY + "AndroidManifest.xml").write(finalManifest);
	}

}

















