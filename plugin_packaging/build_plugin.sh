#!/bin/bash

current=`pwd`
mkdir -p /tmp/refSHARK/
cp -R ../src /tmp/refSHARK
cp ../pom.xml /tmp/refSHARK
cp * /tmp/refSHARK
cd /tmp/refSHARK/

tar -cvf "$current/refSHARK_plugin.tar" --exclude=*.tar --exclude=build_plugin.sh *
