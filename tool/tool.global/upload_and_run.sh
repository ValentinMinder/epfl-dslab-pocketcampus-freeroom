
if [ -z "$1" ]
then
	echo "usage examples:"
	echo "    ./tool/tool.global/upload_and_run.sh http://dev.pocketcampus.ch/"
	echo "    ./tool/tool.global/upload_and_run.sh https://test-pocketcampus.epfl.ch/dev/kissrv120/"
	echo "    ./tool/tool.global/upload_and_run.sh https://prod-pocketcampus.epfl.ch/dev/kissrv119/"
	echo "    ./tool/tool.global/upload_and_run.sh https://prod-pocketcampus.epfl.ch/dev/kissrv118/"
	exit
fi

server=$1

echo "Access token for $server :"
read token

echo "======================= UPLOADING JAR ======================="

uploadstatus=`curl -F file=@server/PocketCampusServer/PocketCampusServer.jar $server/upload_jar.php?t=$token`

if [[ "$uploadstatus" != "Uploaded"* ]]
then
	echo "Upload failed:" $uploadstatus
	exit 1
fi

echo "==================== STARTING NEW SERVER ===================="

restartstatus=`curl -F field=value $server/restart_server.php?t=$token`

if [[ "$restartstatus" != *"started with pid"* ]]
then
	echo "Could not start new server:" $restartstatus
	exit 1
fi

echo "=========================== DONE  ==========================="

