package org.pocketcampus.tool.build.template

class AndroidDotProjectTemplate {
	static def getText(String applicationName) {
		
		/* BuildCommand for the Merger Builder
		<buildCommand>
			<name>org.eclipse.ui.externaltools.ExternalToolBuilder</name>
			<triggers>full,incremental,</triggers>
			<arguments>
				<dictionary>
					<key>LaunchConfigHandle</key>
					<value>&lt;project&gt;/.externalToolBuilders/PocketCampus Merger.launch</value>
				</dictionary>
			</arguments>
		</buildCommand> 
		*/
		
		return  """\
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>""" + applicationName + """</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>com.android.ide.eclipse.adt.ResourceManagerBuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>com.android.ide.eclipse.adt.PreCompilerBuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>org.eclipse.jdt.core.javabuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>com.android.ide.eclipse.adt.ApkBuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>com.android.ide.eclipse.adt.AndroidNature</nature>
		<nature>org.eclipse.jdt.core.javanature</nature>
	</natures>
</projectDescription>
		"""
	}
}
