Run these commands from the repository root.

# Server

## Build  
`./tool/tool.global/merge_and_build_server.sh`

## Run
`java -jar server/PocketCampusServer/PocketCampusServer.jar`


# Android client

## Build for debug  
`./tool/tool.global/merge_and_build_android_dev.sh`

## Build for release
`./tool/tool.global/merge_and_build_android_prod.sh`

## Install
```
adb uninstall org.pocketcampus
adb install android/PocketCampus/bin/PocketCampus-debug.apk 
```
or
```
adb install -r android/PocketCampus/bin/PocketCampus-debug.apk
```
what keeps local data
