# WIP
# Shell Script used for installing Tomcat on OS X
# Assumes the tar is in ~/Downloads/
#
# No previous shell scripting experience, needs testing.

sudo mkdir /usr/local
sudo mv ~/Downloads/apache-tomcat-7.0.27 /usr/local
sudo ln -s /usr/local/apache-tomcat-7.0.27 /Library/Tomcat

export user=$(whoami)

sudo chown -R ${user}/Library/Tomcat
sudo chmod +x /Library/Tomcat/bin/*.sh

~ ${user}/Library/Tomcat/bin/startup.sh