#!/bin/bash

for ID in `cut -f 1 < EcoCloudPosters.txt`
do
	URL="http://chart.apis.google.com/chart?cht=qr&chs=400x400&chl=pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId=23000070%26markFavorite=$ID";
	echo $URL;
	curl -o "${ID}.png" "${URL}"
	composite -gravity NorthEast "$ID.png" ecocloud-poster-2015.png "template${ID}.png"
	rm "${ID}.png"
done	
