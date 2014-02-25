#!/bin/bash

cd $1

output_file="$1/ios/PocketCampus/AutomationScripts/automated-tests-`date | tr ' ' '-'`.log"

echo "output_file=${output_file}"

exec > ${output_file} 2>&1

echo "In `pwd`"

git pull

cd ios/PocketCampus/
echo "In `pwd`"

echo "========Building the .ipa file==========="
ipa build -s PocketCampus -c Debug --verbose 

echo "========Deploying the .ipa file=========="
device="iPhone4-White"
device_id=`cat AutomationScripts/${device}`

cd AutomationScripts

ruby transporter_chief.rb --device ${device_id}

echo "========Running the tests================"
echo "On ${device_id}" 

./runTests.sh PocketCampus `pwd`/PocketCampusAutoMonkey.js `pwd` ${device_id}

