package org.pocketcampus.tool.build.parser

import java.io.File;

public class DotClasspath {
	public static Manifest fromFile(File manifest) {
		// reads Manifest
		def root = new groovy.util.XmlParser().parse(manifest)
		def classpathentry = root.classpathentry

		println ""
		def print = { printer.print(it) }
		//classpathentry.each(print)
		//classpathentry.each {println ("--> "+it.attribute('kind') + ", " + it.attribute('path'))}
		
		return null
	}
}
