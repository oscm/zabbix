
https://share.zabbix.com/cat-app/web-servers/php-fpm-for-zabbix-3-2 




sed -i "s/;pm.status_path/pm.status_path/" /etc/php-fpm.d/www.conf

sed -i "s/;ping/ping/" /etc/php-fpm.d/www.conf



