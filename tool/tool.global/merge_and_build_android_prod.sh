#!/bin/sh

php tool/tool.merger/merger.php 
ant -f android/PocketCampus/build.xml clean
cp android/PocketCampus/res/raw/pocketcampus_prod.config android/PocketCampus/res/raw/pocketcampus.config
ant -f android/PocketCampus/build.xml release

