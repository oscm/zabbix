Zabbix for Postfix 3.2
=====

Install agent
-----

	yum install -y logcheck
	mkdir -p /srv/zabbix/libexec
	wget https://raw.githubusercontent.com/oscm/zabbix/master/postfix/postfix -P /srv/zabbix/libexec
	chmod +x /srv/zabbix/libexec/postfix

	wget https://raw.githubusercontent.com/oscm/zabbix/master/postfix/userparameter_postfix.conf -P /etc/zabbix/zabbix_agentd.d/
	systemctl restart zabbix-agent

	zabbix_get -s 13.24.22.53 -k 'agent.ping'


Packages needed
-----
for Oracle Enterprise Linux 7:
- lockfile-progs-0.1.15-7.el7.x86_64.rpm
- perl-mime-construct-1.11-13.el7.noarch.rpm
- logcheck-1.3.15-2.el7.noarch.rpm
