package org.pocketcampus.tool.build

class Creator {
	static String mAndroidDir = "../../plugin/"

	static String mAndroidTemplate = "template/android";
	static String mSharedTemplate = "template/shared"
	static String mServerTemplate = "template/server"
	static String mTempDir = "temp/"

	public static void main(String[] args) {
//		System.out.println("Plugin name?");
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
//		String pluginName = br.readLine()
//
//		String createSharedStr = "";
//		while(createSharedStr!="y" && createSharedStr!="n") {
//			System.out.println("Create server and shared project? y/n");
//			createSharedStr = br.readLine()
//		}
//
//		boolean createShared = (createSharedStr=="y")

		String pluginName = "foo"
		boolean createShared = true
		String pluginNameLower = pluginName.toLowerCase()

		if(createAndroidProject(pluginNameLower)) {
			println "Done!"
			return
		}

		println "Failed."
	}

	static boolean createAndroidProject(String pluginName) {
		System.out.print("Creating Android project... ")

		String tempDir = mTempDir + "temp" +  (int)(Math.random() * 10000) + "/";
		String finalDir = mAndroidDir + pluginName + "/plugin." + pluginName + ".android/";

		if(new File(finalDir).exists()) {
			println "plugin '" + pluginName + "' already exists!"
			return false;
		}

		FileUtils.copyDirectory(mAndroidTemplate, tempDir)

		// perform substitutions
		new File(tempDir + "src/org/pocketcampus/plugin/test").renameTo(tempDir + "src/org/pocketcampus/plugin/" + pluginName)

		FileUtils.copyDirectory(tempDir, finalDir)

		System.out.println("done");

		if(!new File(tempDir).deleteDir()) {
			//			println "Warning: couldn't delete temp files."
		}

		return true;
	}
}















