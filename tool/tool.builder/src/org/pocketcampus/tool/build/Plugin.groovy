package org.pocketcampus.tool.build

import org.pocketcampus.tool.build.template.AndroidDotProjectTemplate;
import org.pocketcampus.tool.build.template.JavaDotProjectTemplate;
import org.pocketcampus.tool.build.utils.FileUtils;
import org.pocketcampus.tool.build.parser.Manifest;
import org.pocketcampus.tool.build.parser.DotClasspath;

class Plugin {
	def String mName
	
	def File mDirectory
	def File mAndroidDirectory
	def File mSharedDirectory
	def File mServerDirectory
	def File mManifestFile
	def File mDotClasspathFile
	def File mProjectDotPropertiesFile
	
	def Manifest mManifest
	def DotClasspath mDotClassPath
	
	static Plugin fromDirectory(File directory) {
		return new Plugin(directory)
	}
	
	private Plugin(File directory) {
		mDirectory = directory
		mName = getName()
	}
	
	public void parse() {
		checkFiles()
		
		print "Parsing android manifest... "
		mManifest = Manifest.fromFile(mManifestFile)
		
		print "Parsing android .classpath"
		mDotClassPath = DotClasspath.fromFile(mDotClasspathFile)
		
//		print "Parsing shared .classpath"
//		mDotClassPath = DotClasspath.fromFile(mDotClasspathFile)
	}
	
	public void copyTo(String targetAndroidPath, String targetSharedPath, String targetServerPath) {
		copyAndroidFilesTo(targetAndroidPath)
		copySharedFilesTo(targetSharedPath)
		copyServerFilesTo(targetServerPath)
	}
	
	private void copyAndroidFilesTo(String targetPath) {
		print "Merging android files... "
		
		// copy source files
		FileUtils.copyDirectory(new File(mAndroidDirectory.getPath() + "/src"), new File(targetPath + "/src"))
		
		// copy resources
		FileUtils.copyDirectory(new File(mAndroidDirectory.getPath() + "/res"), new File(targetPath + "/res"))
		
		// create gen directory
		new File(targetPath + "/gen").mkdir()
		
		// create .project
		String dotProjectText = AndroidDotProjectTemplate.getText(ApplicationBuilder.APPLICATION_NAME_ANDROID)
		new File(targetPath + "/.project").write(dotProjectText)
		
//		// create merged .classpath
		String dotClasspath = ""
		new File(targetPath + "/.classpath").write(dotClasspath)
		
		println "done."
	}
	
	private void copySharedFilesTo(String targetPath) {
		print "Merging shared files... "
		
		// copy source files
		FileUtils.copyDirectory(new File(mSharedDirectory.getPath() + "/src"), new File(targetPath + "/src"))
		
		// create bin directory
		new File(targetPath + "/bin").mkdir()
		
		// create .project
		String dotProjectText = JavaDotProjectTemplate.getText(ApplicationBuilder.APPLICATION_NAME_SHARED)
		new File(targetPath + "/.project").write(dotProjectText)
		
		// create merged .classpath
		// TODO
		
		println "done."
	}
	
	private void copyServerFilesTo(String targetServerPath) {
		print "Merging server files... "
		// TODO
		println "done."
	}
	
	private void checkFiles() {
		mAndroidDirectory = new File(mDirectory.getPath() + "/plugin." + mName + ".android")
		checkFile(mAndroidDirectory, "Android directory")
		
		mSharedDirectory = new File(mDirectory.getPath() + "/plugin." + mName + ".shared")
		checkFile(mSharedDirectory, "Shared directory")
		
		mServerDirectory = new File(mDirectory.getPath() + "/plugin." + mName + ".server")
		checkFile(mServerDirectory, "Server directory")
		
		mManifestFile = new File(mAndroidDirectory.getPath() + "/AndroidManifest.xml")
		checkFile(mManifestFile, "Android Manifest")
		
		mDotClasspathFile = new File(mAndroidDirectory.getPath() + "/.classpath")
		checkFile(mDotClasspathFile, ".classpath")
		
		mProjectDotPropertiesFile = new File(mAndroidDirectory.getPath() + "/project.properties")
		checkFile(mProjectDotPropertiesFile, ".properties")
	}
	
	
	private void checkFile(File file, String desc) {
		if(!file.exists()) {
			throw new IllegalArgumentException("Plugin's " + desc + " doesn't exist!");	
		}
	}
	
	private String getName() {
		String path = mDirectory.getPath()
		return path.subSequence(path.lastIndexOf("\\")+1, path.length())
	}
}