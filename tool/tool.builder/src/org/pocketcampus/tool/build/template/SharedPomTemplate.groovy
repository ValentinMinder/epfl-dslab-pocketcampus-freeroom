package org.pocketcampus.tool.build.template

class SharedPomTemplate {
	static def getText() {
		return  """\
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>merged.shared</artifactId>
	<name>PocketCampus Application (Shared)</name>

	<parent>
		<groupId>org.pocketcampus</groupId>
		<artifactId>pocketcampus</artifactId>
		<version>0.3-SNAPSHOT</version>
		<relativePath>../../</relativePath>
	</parent>
	
    <dependencies>
		<dependency>
			<groupId>org.pocketcampus</groupId>
			<artifactId>platform.sdk.shared</artifactId>
			<version>0.3-SNAPSHOT</version>
		</dependency>
		
        <dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>1.6.2</version>
			<type>jar</type>
		</dependency>

		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-simple</artifactId>
			<version>1.6.2</version>
			<type>jar</type>
		</dependency>

		<dependency>
			<groupId>org.apache.thrift</groupId>
			<artifactId>libthrift</artifactId>
			<version>0.7.0</version>
		</dependency>
    </dependencies>

	<build>
		<sourceDirectory>src</sourceDirectory>
	</build>
	
</project>
"""
	}
}
