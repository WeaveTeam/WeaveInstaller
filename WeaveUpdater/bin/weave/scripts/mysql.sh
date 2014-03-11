# WIP
# Shell Script used for installing MySQL on OS X
# Assumes the tar is in ~/Downloads/
#
# No previous shell scripting experience, needs testing.

sudo groupadd mysql
sudo useradd -r -g mysql mysql
sudo cd /usr/local

#64-bit 10.6 Version
#Not sure what the path/to should refer to, possibly ~/Downloads
sudo tar zxvf /path/to/mysql-5.5.25a-osx10.6-x86_64.tar.gz

#Obviously full-path-to-mysql-VERSION-OS refers to the above path
sudo ln -s full-path-to-mysql-VERSION-OS mysql

sudo cd mysql
sudo chown -R mysql .
sudo chgrp -R mysql .
sudo scripts/mysql_install_db --user=mysql
sudo chown -R root .
sudo chown -R mysql data

# Next command is optional
sudo cp support-files/my-medium.cnf /etc/my.cnf
sudo bin/mysqld_safe --user=mysql &

# Next command is optional
sudo cp support-files/mysql.server /etc/init.d/mysql.server

#Following commands are post-installation instructions copied from the MySQL dev manual
sudo cd /usr/local/mysql
sudo chown -R mysql .
sudo chgrp -R mysql .

#Additional instructions may be necessary for the following command.
#See http://dev.mysql.com/doc/refman/5.6/en/unix-postinstallation.html for more info
sudo scripts/mysql_install_db --user=mysql

sudo chown -R root .
sudo chown -R mysql data

#Start the server as user mysql
sudo bin/mysqld_safe --user=mysql &

#Check if the server is working
sudo bin/mysqladmin version
sudo bin/mysqladmin variables
