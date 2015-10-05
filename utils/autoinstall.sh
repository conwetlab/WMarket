#!/bin/bash

export MYSQL_PASS=root
export WEBAPPS_PATH=/var/lib/tomcat7/webapps

# Avoid interactivity (e.g. ask for a root password when installing MySQL)
export DEBIAN_FRONTEND=noninteractive

# Exit on failure
set -e


#################################################
#################### GET WAR ####################
#################################################

# Use GitHub API to get latest release
#   1.- Check GitHub API
#   2.- Get browser_download_url
#   3.- Remove json field, quotes, colons and white spaces
export URL_DOWNLOAD_LATEST=`curl https://api.github.com/repos/conwetlab/WMarket/releases/latest 2>/dev/null | grep browser_download_url | sed 's/.*"browser_download_url": "\(.*\)".*/\1/'`
wget $URL_DOWNLOAD_LATEST


#################################################
########### DEPENDENCIES INSTALLATION ###########
#################################################

# Install 
sudo apt-get update -q
sudo apt-get install -q -y unzip

# Install MySQL
# Avoid installation script to ask for a password
sudo -E apt-get install -q -y mysql-server mysql-client
# Set root password
sudo mysqladmin -u root password $MYSQL_PASS

# Install Java
sudo apt-get install -q -y openjdk-7-jdk

# Install Tomcat
sudo apt-get install -y -q tomcat7 tomcat7-docs tomcat7-admin

# Start up
sudo service mysql restart
sudo service tomcat7 stop


#################################################
################# CONFIGURATION #################
#################################################

# Create Marketplace Database
mysqladmin -u root -p$MYSQL_PASS create marketplace

# Unzip WMarket
unzip -q WMarket.war -d WMarket

# Configure Marketplace
sed -i "s|^jdbc.username.*$|jdbc.username=root|g" WMarket/WEB-INF/classes/properties/database.properties
sed -i "s|^jdbc.password.*$|jdbc.password=$MYSQL_PASS|g" WMarket/WEB-INF/classes/properties/database.properties

# Index
export PATH_INDEX=/opt/index

sed -i "s|lucene.IndexPath=.*$|lucene.IndexPath=$PATH_INDEX|g" WMarket/WEB-INF/classes/properties/marketplace.properties

sudo mkdir $PATH_INDEX
sudo chmod a+rw $PATH_INDEX

# Media Files
export PATH_MEDIA=/opt/media
# 3 MB
export MAX_SIZE_MEDIA_FILES=3145728

sed -i "s|^media.folder.*$|media.folder=$PATH_MEDIA|g" WMarket/WEB-INF/classes/properties/marketplace.properties
sed -i "s|^media.maxSize.*$|media.maxSize=$MAX_SIZE_MEDIA_FILES|g" WMarket/WEB-INF/classes/properties/marketplace.properties

sudo mkdir $PATH_MEDIA
sudo chmod a+rw $PATH_MEDIA

# Descriptions Autoupdate (24 hours)
export PERIOD_UPDATE_DESCRIPTIONS=43200

sed -i "s|^descriptions.updatePeriod.*$|descriptions.updatePeriod=$PERIOD_UPDATE_DESCRIPTIONS|g" WMarket/WEB-INF/classes/properties/marketplace.properties

# Update war file
cd WMarket
sudo jar uf ../WMarket.war WEB-INF/classes/properties/database.properties WEB-INF/classes/properties/marketplace.properties WEB-INF/classes/spring/config/BeanLocations.xml
cd ..


#################################################
################### DEPLOYMENT ##################
#################################################

sudo chmod a+r WMarket.war
sudo cp -p WMarket.war $WEBAPPS_PATH/WMarket.war
sudo service tomcat7 start


#################################################
################## WAIT TOMCAT ##################
#################################################

tail -f /var/log/tomcat7/catalina.out | while read LOGLINE
do
   [[ "${LOGLINE}" == *"Server startup"* ]] && pkill -P $$ tail
done