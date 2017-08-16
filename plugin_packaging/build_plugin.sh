#!/bin/bash

current=`pwd`
mkdir -p /tmp/refSHARK/
cp ../target/refSHARK.jar /tmp/refSHARK/
cd /tmp/refSHARK/

tar -cvf "$current/refSHARK_plugin.tar"
