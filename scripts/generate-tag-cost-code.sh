#!/bin/bash

cat tags.txt | awk -F= '{ print "entry(\"" $1 "\", " $2 ")," }' > tags.java-ish
