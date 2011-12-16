#!/bin/bash

# timer utility function
function timer() {
    if [[ $# -eq 0 ]]; then
        echo $(date '+%s')
    else
        local  stime=$1
        etime=$(date '+%s')

        if [[ -z "$stime" ]]; then stime=$etime; fi

        dt=$((etime - stime))
        ds=$((dt % 60))
        dm=$(((dt / 60) % 60))
        dh=$((dt / 3600))
        printf '%02dm%02ds' $dm $ds
    fi
}

tmr=$(timer)

# makes sure we got the path to the android sdk
if [ -z "$ANDROID_HOME" -o ! -z "$1" ]
then
  if [ -z "$1" ]
  then
    echo "Usage: build <android_home>"
    exit 1
  fi 
  echo "Using Android SDK from: $1"
  echo "" >> ~/.bashrc
  echo "export ANDROID_HOME=$1" >> ~/.bashrc
  export ANDROID_HOME=$1
fi

echo "Cleaning up…"
mvn clean 

echo "Merging the plugins..."
cd tool/tool.builder/ 
ant 
cd ../../

echo "Building the application…"
mvn install

echo "Uninstalling old app…"
adb uninstall org.pocketcampus

echo "Installing new app..."
adb install merged/android/target/merged.android-0.3-SNAPSHOT.apk

printf 'All done, took %s\n' $(timer $tmr) 
