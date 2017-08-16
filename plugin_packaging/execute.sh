#!/bin/sh
PLUGIN_PATH=$1
REPOSITORY_PATH=$2
NEW_UUID=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)

cp -R $REPOSITORY_PATH "/dev/shm/$NEW_UUID"

COMMAND="java -jar $PLUGIN_PATH/refSHARK.jar --input /dev/shm/$NEW_UUID --rev $3 --url $4 --db-hostname $5 --db-port $6 --db-database $7"

if [ ! -z ${4+x} ] && [ ${4} != "None" ]; then
    COMMAND="$COMMAND --debug ${4}"
fi

if [ ! -z ${8+x} ] && [ ${8} != "None" ]; then
	COMMAND="$COMMAND --db-user ${8}"
fi

if [ ! -z ${9+x} ] && [ ${9} != "None" ]; then
	COMMAND="$COMMAND --db-password ${9}"
fi

if [ ! -z ${10+x} ] && [ ${10} != "None" ]; then
	COMMAND="$COMMAND --db-authentication ${10}"
fi

if [ ! -z ${11+x} ] && [ ${11} != "None" ]; then
	COMMAND="$COMMAND -ssl"
fi


$COMMAND

rm -rf "/dev/shm/$NEW_UUID"
