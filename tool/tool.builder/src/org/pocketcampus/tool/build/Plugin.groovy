package org.pocketcampus.tool.build

import org.pocketcampus.tool.build.template.AndroidDotProjectTemplate;
import org.pocketcampus.tool.build.template.SharedDotProjectTemplate;
import org.pocketcampus.tool.build.utils.FileUtils;
import org.pocketcampus.tool.build.parser.Manifest;
import org.pocketcampus.tool.build.parser.DotClasspath;

class Plugin {
	private String mName
	
	private File mDirectory
	private File mAndroidDirectory
	private File mSharedDirectory
//	private File mServerDirectory
	private File mManifestFile
	private File mDotClasspathFile
	private File mProjectDotPropertiesFile
	
	private Manifest mManifest
	private DotClasspath mDotClassPath
	
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
	
	public Manifest getManifest() {return mManifest}
	public DotClasspath getDotClasspath() {return mDotClassPath}
	
	public void copyTo(String targetAndroidPath, String targetSharedPath, String targetServerPath) {
		copyAndroidFilesTo(targetAndroidPath)
		copySharedFilesTo(targetSharedPath)
//		copyServerFilesTo(targetServerPath)
	}
	
	private void copyAndroidFilesTo(String targetPath) {
		print "Merging android files... "
		
		// copy source files
		FileUtils.copyDirectory(new File(mAndroidDirectory.getPath() + "/src"), new File(targetPath + "/src"))
		
		// copy resources
		FileUtils.copyDirectory(new File(mAndroidDirectory.getPath() + "/res"), new File(targetPath + "/res"))
		
		// create gen directory
		new File(targetPath + "/gen").mkdir()
		
		// create assets directory
		new File(targetPath + "/assets").mkdir()
		
		// create .project
		String dotProjectText = AndroidDotProjectTemplate.getText(ApplicationBuilder.APPLICATION_NAME_ANDROID)
		new File(targetPath + "/.project").write(dotProjectText)
		
		println "done."
	}
	
	private void copySharedFilesTo(String targetPath) {
		print "Merging shared files... "
		
		// copy source files
		FileUtils.copyDirectory(new File(mSharedDirectory.getPath() + "/src"), new File(targetPath + "/src"))
		
		// copy jars
		//FileUtils.copyDirectory(new File(mSharedDirectory.getPath() + "/lib"), new File(targetPath + "/lib"))
		
		// create bin directory
		new File(targetPath + "/bin").mkdir()
		
		// create .project
		String dotProjectText = SharedDotProjectTemplate.getText(ApplicationBuilder.APPLICATION_NAME_SHARED)
		new File(targetPath + "/.project").write(dotProjectText)
		
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
		
//		mServerDirectory = new File(mDirectory.getPath() + "/plugin." + mName + ".server")
//		checkFile(mServerDirectory, "Server directory")
		
		mManifestFile = new File(mAndroidDirectory.getPath() + "/AndroidManifest.xml")
		checkFile(mManifestFile, "Android Manifest")
		
		mDotClasspathFile = new File(mAndroidDirectory.getPath() + "/.classpath")
		checkFile(mDotClasspathFile, ".classpath")
		
		mProjectDotPropertiesFile = new File(mAndroidDirectory.getPath() + "/project.properties")
		checkFile(mProjectDotPropertiesFile, ".properties")
		
		mProjectDotPropertiesFile.eachLine {
			if(it.indexOf("android.library=true") != -1)
				throw new IllegalArgumentException("This plugin is an Android library, expected stand-alone plugin.");
		}
	}
	
	
	private void checkFile(File file, String desc) {
		if(!file.exists()) {
			println file.getPath();
			throw new IllegalArgumentException("Plugin's " + desc + " doesn't exist!");	
		}
	}
	
	private String getName() {
		String path = mDirectory.getPath()
		// Be cross-platform
		Integer lastIndexOfSlash = Math.max(path.lastIndexOf("\\"), path.lastIndexOf("/"))
		return path.subSequence(lastIndexOfSlash + 1, path.length())
	}
}