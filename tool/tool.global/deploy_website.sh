#!/bin/bash

# This script will pull from git on the given machine
# and deploy the website.

if [ -z "$1" ]
then
	echo "Usage options:"
	echo "    ./tool/tool.global/deploy_website.sh dev";
	echo "    ./tool/tool.global/deploy_website.sh prod1";
	echo "    ./tool/tool.global/deploy_website.sh prod2";
	exit
fi

if [ "$1" == "dev" ]
then
	server="kissrv120.epfl.ch"
elif [ "$1" == "prod1" ]
then
	server="kissrv118.epfl.ch"
elif [ "$1" == "prod2" ]
then
	server="kissrv119.epfl.ch"
else
	echo "Unknown server (possible options: dev, prod1, prod2)";
	exit
fi

ssh pocketcampus@$server 'cd pocketcampus; git pull; cp -r website/* /var/www/vhosts/pocketcampus/htdocs/'