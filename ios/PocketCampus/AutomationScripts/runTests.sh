#!/bin/bash

APP_NAME="$1"
echo "APP_NAME=${APP_NAME}"
DIR=`find /Users/${USER}/Library/Application\ Support/iPhone\ Simulator/7.0.3/Applications/ -name "${APP_NAME}"`; echo $DIR; DIR=`dirname "$DIR"`; echo $DIR; rm -rf "$DIR"

XCODE_DIR=`dirname ${DEVELOPER_DIR}`
TEMPLATE="{XCODE_DIR}/Applications/Instruments.app/Contents/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate"

echo "About to run: ${XCODE_DIR}/Developer/usr/bin/instruments -t ${XCODE_DIR}/Applications/Instruments.app/Contents/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate /tmp/${APP_NAME} -e UIASCRIPT $2 -e UIARESULTSPATH $3"
${XCODE_DIR}/Developer/usr/bin/instruments -t ${XCODE_DIR}/Applications/Instruments.app/Contents/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate /tmp/${APP_NAME} -e UIASCRIPT $2 -e UIARESULTSPATH $3
