This file contains the change to make to merger_server.php in order to make FreeRoom server work with the rest of PocketCampus server.
The real file is not modified to avoid git conflicts. Merge must be done manually after pull request time, to integrate FreeRoom server

// lines 18-32, adding ", "EWSJavaAPI_1.2.jar");"
$libs_to_export = array(
		"backport-util-concurrent-3.1.jar", "bcprov-jdk15-146.jar", 
		"commons-codec-1.4.jar", "commons-io-2.0.1.jar", "commons-lang-2.6.jar", "commons-lang3-3.0.1.jar", "commons-logging-1.1.1.jar", 
		"gson-1.7.1.jar", "gcm-server.jar", 
		"httpclient-4.1.2.jar", "httpclient-cache-4.1.2.jar", "httpcore-4.1.2.jar", "httpmime-4.1.2.jar", 
		"ical4j-1.0.4.jar", 
		"javapns_2.2.jar",
		"jetty-ajp-8.0.0.M3.jar", "jetty-annotations-8.0.0.M3.jar", "jetty-client-8.0.0.M3.jar", "jetty-continuation-8.0.0.M3.jar", "jetty-deploy-8.0.0.M3.jar", "jetty-http-8.0.0.M3.jar", "jetty-io-8.0.0.M3.jar", "jetty-jmx-8.0.0.M3.jar", "jetty-jndi-8.0.0.M3.jar", "jetty-overlay-deployer-8.0.0.M3.jar", "jetty-plus-8.0.0.M3.jar", "jetty-policy-8.0.0.M3.jar", "jetty-rewrite-8.0.0.M3.jar", "jetty-security-8.0.0.M3.jar", "jetty-server-8.0.0.M3.jar", "jetty-servlet-8.0.0.M3.jar", "jetty-servlets-8.0.0.M3.jar", "jetty-util-8.0.0.M3.jar", "jetty-webapp-8.0.0.M3.jar", "jetty-websocket-8.0.0.M3.jar", "jetty-xml-8.0.0.M3.jar",
		"json_simple-1.1.jar", "jsoup-1.7.2.jar", 
		"joda-time-2.3.jar",
		"kxml2-2.3.0.jar", 
		"log4j-1.2.16.jar", 
		"mail.jar", "mysql-connector-java-5.1.15-bin.jar", 
		"servlet-api-3.0.jar", "slf4j-api-1.6.2.jar", "slf4j-simple-1.6.2.jar", 
		"unboundid-ldapsdk-se.jar", "EWSJavaAPI_1.2.jar");

		// line 208, adding "|| $entry == 'tests') "
		if ($entry == '.' || $entry == '..' || $entry == 'tests') {
			continue;
		}
