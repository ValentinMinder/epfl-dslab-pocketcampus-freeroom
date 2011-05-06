#!/bin/sh

mkdir -p png

cd svg

for f in *.svg; do
	image=`basename "$f" ".svg"`
	inkscape -f "$f" -w 40 -h 72 -e "../png/${image}.png"
done

