#!/bin/bash

HOST="127.0.0.1"
PORT="27017"
USER=monitor
PASS=chen

index=$(echo $@ | tr " " ".")

status=$(echo "db.serverStatus().${index}" |mongo -u ${USER} -p ${PASS} admin --port ${PORT}|sed -n '3p')
 
#check if the output contains "NumberLong"
if [[ "$status" =~ "NumberLong"   ]];then
	echo $status|sed -n 's/NumberLong(//p'|sed -n 's/)//p'
else 
	echo $status
fi
