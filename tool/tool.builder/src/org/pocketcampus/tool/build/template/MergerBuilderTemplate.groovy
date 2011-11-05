package org.pocketcampus.tool.build.template

class MergerBuilderTemplate {
	static def getText() {
		return  """\
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<launchConfiguration type="org.eclipse.ant.AntBuilderLaunchConfigurationType">
<booleanAttribute key="org.eclipse.ant.ui.ATTR_TARGETS_UPDATED" value="true"/>
<booleanAttribute key="org.eclipse.ant.ui.DEFAULT_VM_INSTALL" value="false"/>
<stringAttribute key="org.eclipse.debug.core.ATTR_REFRESH_SCOPE" value="\${working_set:&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#13;&#10;&lt;resources&gt;&#13;&#10;&lt;item path=&quot;/pocketcampus-android&quot; type=&quot;4&quot;/&gt;&#13;&#10;&lt;item path=&quot;/pocketcampus-shared&quot; type=&quot;4&quot;/&gt;&#13;&#10;&lt;/resources&gt;}"/>
<booleanAttribute key="org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND" value="false"/>
<stringAttribute key="org.eclipse.jdt.launching.CLASSPATH_PROVIDER" value="org.eclipse.ant.ui.AntClasspathProvider"/>
<booleanAttribute key="org.eclipse.jdt.launching.DEFAULT_CLASSPATH" value="true"/>
<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="tool.builder"/>
<stringAttribute key="org.eclipse.ui.externaltools.ATTR_LOCATION" value="\${workspace_loc:/tool.builder/build.xml}"/>
<stringAttribute key="org.eclipse.ui.externaltools.ATTR_RUN_BUILD_KINDS" value="clean"/>
<booleanAttribute key="org.eclipse.ui.externaltools.ATTR_TRIGGERS_CONFIGURED" value="true"/>
</launchConfiguration>
"""
	}
}
