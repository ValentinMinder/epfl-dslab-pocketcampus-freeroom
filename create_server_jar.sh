#!/bin/sh

php tool/tool.merger/merger_server.php 
ant -f server/PocketCampusServer/build.xml clean
ant -f server/PocketCampusServer/build.xml create_run_jar