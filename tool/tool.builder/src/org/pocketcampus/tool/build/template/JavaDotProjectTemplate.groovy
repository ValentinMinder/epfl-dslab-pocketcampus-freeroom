package org.pocketcampus.tool.build.template

class JavaDotProjectTemplate {
	static def getText(String applicationName) {
		return  """\
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>""" + applicationName + """</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>org.eclipse.jdt.core.javabuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.jdt.core.javanature</nature>
	</natures>
</projectDescription>
		"""
	}
}
