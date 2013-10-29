echo "1. Merging..";

php tool/tool.merger/merger_server.php

if [ $? -ne 0 ]; then
	echo "!! Merger tool failed. Aborting.";
fi

echo "2. Cleaning...";

ant -f server/PocketCampusServer/build.xml clean

if [ $? -ne 0 ]; then
	echo "!! ant clean failed. Aborting.";
fi

echo "3. Compiling..";

ant -f server/PocketCampusServer/build.xml create_run_jar

if [ $? -ne 0 ]; then
	echo "!! ant create_run_har failed. Aborting.";
else	
	echo "Done. JAR is ready.";
fi