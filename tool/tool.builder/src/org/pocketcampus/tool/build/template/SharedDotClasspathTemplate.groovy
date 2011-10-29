package org.pocketcampus.tool.build.template

class SharedDotClasspathTemplate {
	static def getText() {
		return  """\
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
	<classpathentry kind="src" path="src"/>
	<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
	<classpathentry kind="lib" path="/platform.sdk.shared/lib/libthrift-0.7.0-multiplex.jar" sourcepath="/platform.sdk.shared/lib/libthrift-0.7.0-multiplex-sources/META-INF"/>
	<classpathentry kind="lib" path="/platform.sdk.shared/lib/slf4j-api-1.6.2.jar"/>
	<classpathentry kind="lib" path="/platform.sdk.shared/lib/slf4j-simple-1.6.2.jar"/>
	<classpathentry kind="lib" path="/platform.sdk.shared/lib/commons-lang-2.6.jar"/>
	<classpathentry kind="src" path="/platform.sdk.shared"/>
	<classpathentry kind="output" path="bin"/>
</classpath>
		"""
	}
}
