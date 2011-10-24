package org.pocketcampus.tool.build

import java.io.File;

class Manifest {
	String text
	String pkg
	
	private Manifest(pluginManifest, pluginPackage) {
		this.text = pluginManifest;
		this.pkg = pluginPackage;	
	}
	
	public static Manifest fromFile(File manifest) {
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
		
		String manifestText = '\n<!-- PLUGIN ' + pkg.toUpperCase() + " -->\n" + writer.toString()
		
		// log
		println app.activity.size() + " activity, " + app.service.size() + " service, " + app.receiver.size() + " receiver."
		
		return new Manifest(manifestText, pkg);
	}
}
