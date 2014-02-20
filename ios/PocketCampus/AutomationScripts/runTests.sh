#!/bin/bash

if [ $# -eq 0 ]
then
	echo "usage: runTest.sh APP_NAME(full path to name.app) SCRIPT(full path to script) RESULT PATH(full path) DEVICE IDENTIFIER"
	exit
fi

APP_NAME="$1"
DEVICE_IDENTIFIER="$4"
echo "APP_NAME=${APP_NAME}"

if [ -z "${DEVELOPER_DIR}" ]
	then
	XCODE_DIR=`xcode-select --print-path`
	XCODE_DIR=`dirname ${XCODE_DIR}`
else
	XCODE_DIR=`dirname ${DEVELOPER_DIR}`
fi
echo "XCODE_DIR=${XCODE_DIR}"

TEMPLATE="{XCODE_DIR}/Applications/Instruments.app/Contents/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate"

if [ -z "${DEVICE_IDENTIFIER}" ]
	then
	echo "Removing the app from the simulator"
	DIR=`find /Users/${USER}/Library/Application\ Support/iPhone\ Simulator/7.0.3/Applications/ -name "${APP_NAME}"`; 
	echo $DIR; 
	DIR=`dirname "$DIR"`; 
	echo $DIR; 
	rm -rf "$DIR"
	${XCODE_DIR}/Developer/usr/bin/instruments -t ${XCODE_DIR}/Applications/Instruments.app/Contents/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate /tmp/${APP_NAME} -e UIASCRIPT $2 -e UIARESULTSPATH $3
else
	${XCODE_DIR}/Developer/usr/bin/instruments -t ${XCODE_DIR}/Applications/Instruments.app/Contents/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate -w "${DEVICE_IDENTIFIER}" ${APP_NAME} -e UIASCRIPT $2 -e UIARESULTSPATH $3
fi