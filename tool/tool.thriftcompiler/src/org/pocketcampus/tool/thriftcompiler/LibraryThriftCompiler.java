package org.pocketcampus.tool.thriftcompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Tools to automatically compile all thrift file and put them in their respective locations.
 * 
 * @author florian
 */
public class LibraryThriftCompiler {
	private static boolean modeVerbose = false;

	public static void main(String argv[]) {
		boolean errorOccured = false;
		
		File[] files = new File("service/").listFiles();
		for (int i = 0; i < files.length; i++) {
			if(compileFile(files[i], true)) {
				errorOccured = true;
			}
		}
		
		files = new File("include/").listFiles();
		for (int i = 0; i < files.length; i++) {
			if(compileFile(files[i], false)) {
				errorOccured = true;
			}
		}
		
		if(errorOccured) {
			System.out.println();
			System.out.println("=== Error occured! ===");
		} else {
			System.out.println("Done.");
		}
	}
	
	private static boolean compileFile(File file, boolean service) {
		boolean errorOccured = false;
		boolean outputOccured = false;
		String verbose = modeVerbose ?"-v ":"";
		
		if(file.getName().equals(".svn")){
			return false;
		}
		
		System.out.print("Compiling " + file + "... ");
		String pluginName = extractPluginName(file.getName());
		
		if(pluginName.equals("")) {
			System.out.println("invalid name!");
			return false;
		}
		
		try {
			String line;
			
			String directoryIn = service?"service":"include";
			
			String directoryOut = "../../platform/sdk/platform.sdk.shared/src";
			if(service) {
				directoryOut = "../../plugin/"+pluginName+"/plugin."+pluginName+".shared/src";
			}
			
			String command = LocalConfig.THRIFT_PATH + " "+verbose+"--gen java:hashcode -out "+directoryOut+" "+directoryIn+"/"+pluginName+".thrift";
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader inputInfo = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader inputError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			while ((line = inputInfo.readLine()) != null && !line.equals("")) {
				if(!outputOccured) {
					System.out.println();
					outputOccured = true;
				}
				
				System.out.println("   "+line);
			}
			
			while ((line = inputError.readLine()) != null && !line.equals("")) {
				if(!outputOccured) {
					System.out.println();
					outputOccured = true;
					errorOccured = true;
				}
				
				System.out.println("   "+line);
			}
			
			p.waitFor();
			
			if(p.exitValue() == 0) {
				System.out.println("ok.");
				if(modeVerbose) System.out.println();
			} else {
				System.out.println();
			}
			
			inputError.close();
			
			
		} catch (Exception err) {
			System.out.println();
			System.out.print("Compilation failed! ");
			System.out.println(err.getMessage());
		}
		
		return errorOccured;
	}

	private static String extractPluginName(String fileName) {
		int extPos = fileName.indexOf(".thrift");
		
		if(extPos < 1) {
			// invalid name
			return "";
		}
		
		return fileName.substring(0, extPos);
	}
	
}





