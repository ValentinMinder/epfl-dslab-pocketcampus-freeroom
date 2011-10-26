package org.pocketcampus.tool.build.format

import java.io.File;

public class DotClasspath {
	public static Manifest fromFile(File manifest) {
		// reads Manifest
		def root = new groovy.util.XmlParser().parse(manifest)
		print root
	}
}
