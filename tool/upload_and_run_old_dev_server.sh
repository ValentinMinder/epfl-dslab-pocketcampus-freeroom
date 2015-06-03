echo "Username for dslabpc36:"

read user


echo "1. Uploading server..."
echo "scp build/libs/server-all.jar $user@dslabpc36.epfl.ch:/Users/amer/PCS/pocketcampus-server.jar"

scp build/libs/server-all.jar $user@dslabpc36.epfl.ch:/Users/amer/PCS/pocketcampus-server.jar

if [ $? -ne 0 ]; then
	echo "!! Upload failed. Aborting. INFO: you have to run this script from repo's root.";
	exit 1
fi

echo "2. Starting new binary on dev server..."

ssh $user@dslabpc36.epfl.ch '/Users/amer/PCS/deploy_dev_server.sh'