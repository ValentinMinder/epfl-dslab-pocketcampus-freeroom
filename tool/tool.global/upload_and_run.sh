#!/bin/bash

if [ -z "$1" ]
then
	echo "Usage examples:"
#	echo "    ./tool/tool.global/upload_and_run.sh http://dev.pocketcampus.ch/"
#	echo "    ./tool/tool.global/upload_and_run.sh https://test-pocketcampus.epfl.ch/deploy/kissrv120/"
#	echo "    ./tool/tool.global/upload_and_run.sh https://prod-pocketcampus.epfl.ch/deploy/kissrv119/"
#	echo "    ./tool/tool.global/upload_and_run.sh https://prod-pocketcampus.epfl.ch/deploy/kissrv118/"
	echo "    ./tool/tool.global/upload_and_run.sh dev";
	echo "    ./tool/tool.global/upload_and_run.sh prod1";
	echo "    ./tool/tool.global/upload_and_run.sh prod2";
	echo "    ./tool/tool.global/upload_and_run.sh <URL>";
	exit
fi

if [ "$1" == "dev" ]
then
	server="https://test-pocketcampus.epfl.ch/deploy/kissrv120/"
elif [ "$1" == "prod1" ]
then
	server="https://prod-pocketcampus.epfl.ch/deploy/kissrv118/"
elif [ "$1" == "prod2" ]
then
	server="https://prod-pocketcampus.epfl.ch/deploy/kissrv119/"
else
	server=$1
fi

echo "Access token for $server :"
read token

echo "=========================================== UPLOADING JAR ==========================================="

uploadstatus=`curl -F file=@server/PocketCampusServer/PocketCampusServer.jar $server/upload_jar.php?t=$token`

if [[ "$uploadstatus" != "Uploaded"* ]]
then
	echo "Upload failed:" $uploadstatus
	exit 1
fi

echo "======================================== STARTING NEW SERVER ========================================"

restartstatus=`curl -F field=value $server/restart_server.php?t=$token`

if [[ "$restartstatus" != *"started with pid"* ]]
then
	echo "Could not start new server:" $restartstatus
	exit 1
fi

echo "=============================================== DONE  ==============================================="

