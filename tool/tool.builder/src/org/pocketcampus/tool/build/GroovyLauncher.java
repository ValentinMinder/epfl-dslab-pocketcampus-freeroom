package org.pocketcampus.tool.build;

import groovy.lang.GroovyShell;

public class GroovyLauncher {
	public static void main(String[] args) {
		String command = args[0];

		if(command.equals("launcher")) {
			System.out.println("Building launcher...");
			BuildLauncher.main();
			
		} else if(command.equals("plugin")) {
			System.out.println("Building plugin...");
			BuildPlugin.main();
			
		} else {
			System.out.println("Unrecognized command!");
		}
	}

	public static void console(String[] args) {
		GroovyShell.main(args);
	}
}