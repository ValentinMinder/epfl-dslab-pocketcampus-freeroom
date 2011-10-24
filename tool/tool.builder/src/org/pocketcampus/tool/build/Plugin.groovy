package org.pocketcampus.tool.build

class Plugin {
	def String name
	def File directory
	def File androidDirectory
	def File manifestFile
	def File dotClasspath
	def File projectDotProperties
	def Manifest manifest
	
	static Plugin fromDirectory(File directory) {
		return new Plugin(directory)
	}
	
	private Plugin(File directory) {
		this.directory = directory
		this.name = getName()
	}
	
	public void parse() {
		checkFiles()
		
		//print "Parsing manifest... "
		manifest = Manifest.fromFile(manifestFile)
	}
	
	public void copyTo() {
		
	}
	
	
	private void checkFiles() {
		androidDirectory = new File(directory.getPath() + "/plugin." + name + ".android")
		checkFile(androidDirectory, "Android directory")
		
		manifestFile = new File(androidDirectory.getPath() + "/AndroidManifest.xml")
		checkFile(manifestFile, "Android Manifest")
		
		dotClasspath = new File(androidDirectory.getPath() + "/.classpath")
		checkFile(dotClasspath, ".classpath")
		
		projectDotProperties = new File(androidDirectory.getPath() + "/project.properties")
		checkFile(projectDotProperties, ".properties")
	}
	
	
	private void checkFile(File file, String desc) {
		if(!file.exists()) {
			throw new IllegalArgumentException("Plugin's " + desc + " doesn't exist!");	
		}
	}
	
	private String getName() {
		String path = directory.getPath()
		return path.subSequence(path.lastIndexOf("\\")+1, path.length())
	}
}