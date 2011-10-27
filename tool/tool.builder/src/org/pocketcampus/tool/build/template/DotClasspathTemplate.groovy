package org.pocketcampus.tool.build.template

class DotClasspathTemplate {
	static def getText(String classpathEntries) {
		return  """\
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
""" + classpathEntries + """
</classpath>
		"""
	}
}
