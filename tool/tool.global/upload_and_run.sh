
if [ -z "$1" ]
then
	echo "usage examples:"
	echo "    ./tool/tool.global/upload_and_run.sh http://dev.pocketcampus.ch/%s/dummy/?t=%s"
	echo "    ./tool/tool.global/upload_and_run.sh https://test-pocketcampus.epfl.ch/dev/%s/kissrv120/?t=%s"
	echo "    ./tool/tool.global/upload_and_run.sh https://prod-pocketcampus.epfl.ch/dev/%s/kissrv119/?t=%s"
	echo "    ./tool/tool.global/upload_and_run.sh https://prod-pocketcampus.epfl.ch/dev/%s/kissrv118/?t=%s"
	exit
fi

echo "Access token for $1 :"
read token

echo "UPLOADING JAR"

url=`printf $1 upload_jar.php $token`
uploadstatus=`curl -F file=@server/PocketCampusServer/PocketCampusServer.jar $url`

if [[ "$uploadstatus" != "Uploaded"* ]]
then
	echo "Upload failed:" $uploadstatus
	exit 1
fi

echo "STARTING NEW SERVER"

url=`printf $1 restart_server.php $token`
restartstatus=`curl -F field=value $url`

if [[ "$restartstatus" != *"started with pid"* ]]
then
	echo "Could not start new server:" $restartstatus
	exit 1
fi

echo "DONE"

