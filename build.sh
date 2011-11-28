#!/bin/bash
mvn clean 
cd tool/tool.builder/ 
ant 
cd ../../
if [ -z "$ANDROID_HOME" -o ! -z "$1" ]
then
  if [ -z "$1" ]
  then
    echo "build <android_home>"
    exit 1
  fi 
  echo "Building with Android home = $1"
  echo "" >> ~/.bashrc
  echo "export ANDROID_HOME=$1" >> ~/.bashrc
  export ANDROID_HOME=$1
fi
echo "Starting the build"
mvn install
