package org.pocketcampus.tool.build.parser

import java.io.File;
import org.pocketcampus.tool.build.template.DotClasspathTemplate;
import org.pocketcampus.tool.build.ApplicationBuilder

public class DotClasspath {
	def mClasspathEntries = new ArrayList();
	
	public static DotClasspath fromFile(File manifest) {
		// reads Manifest
		def root = new groovy.util.XmlParser().parse(manifest)
		def classpathentry = root.classpathentry

		println ""
		
		def classpathEntries = new ArrayList();
		classpathentry.each {
			classpathEntries.add(new ClasspathEntry(it.attribute('kind'), it.attribute('path')))
		}
				
		return new DotClasspath(classpathEntries)
	}
	
	public static DotClasspath fromEntries(ArrayList classpathEntries) {
		HashSet set = new HashSet(classpathEntries)
		ArrayList list = new ArrayList(set)
		Arrays.sort(list) // doesn't work?
		
		return new DotClasspath(list)
	}

	public void filterEntries() {
		ArrayList classpathToKeep = new ArrayList();
		
		mClasspathEntries.each {
			String src = it.getSrc()
			
			if(src.indexOf(".server") != -1) {
				println "Warning: server code is included in Android plugin! discarded."
			}
			
			if(src.indexOf(".shared")!=-1 || src.indexOf(".jar")!=-1) {
				classpathToKeep.add(it)
			}
		}
		
		mClasspathEntries = classpathToKeep
	}
		
	public String getText() {
		String classpathEntriesXml = ""
		mClasspathEntries.each {
			classpathEntriesXml += "<classpathentry kind=\"" + it.getKind() + "\" path=\"" + it.getSrc() + "\"/>\n"
		}
		
		return DotClasspathTemplate.getText(classpathEntriesXml)
	}
	
	private DotClasspath(ArrayList classpathEntries) {
		mClasspathEntries = classpathEntries
		filterEntries()
	}
	
	public getClasspathEntries() {
		return mClasspathEntries
	}
}
