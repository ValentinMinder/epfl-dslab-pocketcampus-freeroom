package org.pocketcampus.tool.build.template

class AndroidPomTemplate {
	static def getText() {
		return  """\
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<artifactId>merged.android</artifactId>
	<packaging>apk</packaging>
	<name>PocketCampus Application (Android)</name>

	<parent>
		<groupId>org.pocketcampus</groupId>
		<artifactId>pocketcampus</artifactId>
		<version>0.3-SNAPSHOT</version>
		<relativePath>../../</relativePath>
	</parent>

	<dependencies>

		<dependency>
			<groupId>org.pocketcampus</groupId>
			<artifactId>platform.sdk.android</artifactId>
			<version>0.3-SNAPSHOT</version>
			<type>apklib</type>
		</dependency>   
		
		<dependency>
			<groupId>com.google.android</groupId>
			<artifactId>android</artifactId>
			<scope>provided</scope>
		</dependency>

	</dependencies>

	<build>
		<sourceDirectory>src</sourceDirectory>
		<plugins>
			<plugin>
				<groupId>com.jayway.maven.plugins.android.generation2</groupId>
				
				<artifactId>maven-android-plugin</artifactId>
				
				<configuration>
					<sdk>
						<!--  platform or api level (api level 4 = platform 1.6)-->
						<platform>8</platform>
					</sdk>
					<emulator>
						<!--  the name of the avd device to use for starting the emulator-->
						<avd>GoogleAPIs</avd>
					</emulator>
					<deleteConflictingFiles>true</deleteConflictingFiles>
					<undeployBeforeDeploy>true</undeployBeforeDeploy>
					<device>

					</device>
				</configuration>
				
				<extensions>true</extensions>
				
			</plugin>
		</plugins>
	</build>

</project>
"""
	}
}
