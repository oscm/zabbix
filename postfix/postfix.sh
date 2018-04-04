#!/bin/bash
##################################################
# AUTHOR: Neo <netkiller@msn.com>
# WEBSITE: http://www.netkiller.cn
# Description：zabbix agent script
# Note：Zabbix 3.2
# DateTime: 2016-11-29
##################################################
module=$1
item=$2

function usage(){
	name=$(basename $0)
	echo "Postfix collect script for zabbix - http://www.netkiller.cn"
	echo "${name} <module> <item>"
	echo "<queue> <active|deferred>"
	echo "<status> <active|deferred|sent|bounced|expired>"
	echo "<log> <active|deferred|sent|bounced|expired|string>"
	echo "<code> <200|550|421|......>"
	exit
}

if [ -z ${module} ]; then
	usage
fi

if [ ${module} == "discovery" ]; then
	echo '{"data":[{"{#CODE}":"200"},{"{#CODE}":"211"},{"{#CODE}":"214"},{"{#CODE}":"220"},{"{#CODE}":"221"},{"{#CODE}":"250"},{"{#CODE}":"251"},{"{#CODE}":"252"},{"{#CODE}":"354"},{"{#CODE}":"421"},{"{#CODE}":"450"},{"{#CODE}":"451"},{"{#CODE}":"452"},{"{#CODE}":"500"},{"{#CODE}":"501"},{"{#CODE}":"502"},{"{#CODE}":"503"},{"{#CODE}":"504"},{"{#CODE}":"521"},{"{#CODE}":"530"},{"{#CODE}":"550"},{"{#CODE}":"551"},{"{#CODE}":"552"},{"{#CODE}":"553"},{"{#CODE}":"554"}]}'
	exit
else
	if [ -z ${item} ]; then
		usage
	fi 
fi

if [ ${module} == "queue" ]; then
	if [ ${item} == "active" ]; then
		postqueue -p | egrep -c "^[0-9A-F]{10}[*]"
	fi
	if [ ${item} == "deferred" ]; then
		postqueue -p | egrep -c "^[0-9A-F]{10}[^*]"
	fi
elif [ ${module} == "status" ]; then
	status=("active" "deferred" "sent" "bounced" "expired")
	for val in ${status[@]}; do

		if [ $val == $item ]; then
			logtail -f /var/log/maillog -o /var/tmp/postfix.${item}.logtail | grep -c "postfix/smtp.*status=${item}"
		fi
	done
elif [ ${module} == "log" ]; then
	if [ ${item} == "timeout" ]; then
		logtail -f /var/log/maillog -o /var/tmp/postfix.timeout.logtail | grep -c "postfix/smtp.* Connection timed out"
	elif [ ${item} == "unreachable" ]; then
		logtail -f /var/log/maillog -o /var/tmp/postfix.unreachable.logtail | grep -c "Network is unreachable"
	elif [ ${item} == "refused" ]; then
		logtail -f /var/log/maillog -o /var/tmp/postfix.refused.logtail | grep -c "Connection refused"
	fi
elif [ ${module} == "code" ]; then
	logtail -f /var/log/maillog -o /var/tmp/postfix.${item}.logtail | grep -c "said: ${item} "
else 
	usage
fi
