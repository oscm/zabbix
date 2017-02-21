Service Dependency Check
=====

	# mkdir -p /srv/zabbix/libexec
	# wget https://raw.githubusercontent.com/oscm/zabbix/master/Dependency/dependency /srv/zabbix/libexec/
	
    chmod +x /srv/zabbix/libexec/dependency
    
    [root@netkiller zabbix_agentd.d]# /srv/zabbix/libexec/dependency -h
    /srv/zabbix/libexec/dependency - Software dependency check
        author neo <netkiller@msn.com>
        -d/--discovery
        -p/--ping 192.168.0.1/www.netkiller.cn
        -c/--check 192.186.0.1 80
        -h/--help
        
   zabbix_get -s 10.24.15.18 -k 'dependency.discovery'

   
Zabbix Agent
-----
	# wget https://raw.githubusercontent.com/oscm/zabbix/master/Dependency/userparameter_dependency.conf -P /etc/zabbix/zabbix_agentd.d/
	# systemctl restart zabbix-agent
	
	
Config
-----

	mkdir -p /srv/zabbix/conf
	cat >> /srv/zabbix/conf/dependency.conf << EOF
	Redis 127.0.0.1 80
	EOF
