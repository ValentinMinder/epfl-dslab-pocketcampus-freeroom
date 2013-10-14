#!/bin/sh

php tool/tool.merger/merger.php 
ant -f android/PocketCampus/build.xml clean
ant -f android/PocketCampus/build.xml debug


