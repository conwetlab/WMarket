#!/bin/bash

function new_line {
    echo -e "\n"
}

set -e

# Donwload the last version

if [ ! -f "WMarket.war" ]; then
    wget https://github.com/conwetlab/WMarket/releases/download/v4.3.3/WMarket.war
fi

# Check the system distribution
DIST=""

if [ -f "/etc/centos-release" ]; then
    # /etc/centos-release only exists in Debian
    DIST="rhel"
elif [ -f "/etc/issue" ]; then
    # /etc/issue exists in several distributions

    CONTENT=$(cat /etc/issue)

    if [[ $CONTENT == *Ubuntu* || $CONTENT == *Debian* ]]; then
        DIST="deb"
    fi
fi

if [ $DIST == "deb" ]; then
    # Install 
    apt-get update
    apt-get -y install unzip

    set +e
    # Install MySQL
    apt-get -y install mysql-server mysql-client

    # Install Java
    apt-get -y install openjdk-7-jdk

    # Install Tomcat
    apt-get -y install tomcat7 tomcat7-docs tomcat7-admin
    set -e

    WEBAPPS_PATH=/var/lib/tomcat7/webapps

    # Start up
    service mysql restart
    service tomcat7 restart

elif [ $DIST == "rhel" ]; then

    yum -y install unzip

    set +e
    # Install MySQL
    rpm -Uvh http://dev.mysql.com/get/mysql-community-release-el7-5.noarch.rpm
    yum -y install mysql-community-server

    # Install java
    yum -y install java-1.7.0-openjdk-devel

    # Install tomcat
    yum install -y tomcat tomcat-webapps tomcat-admin-webapps
    set -e

    # Database Configuration & Start Up
    systemctl start mysqld
    /usr/bin/mysql_secure_installation

    # Tomcat Start Up
    systemctl start tomcat

    # Variable definitions
    WEBAPPS_PATH=/usr/share/tomcat/webapps
else
    echo "Your operative system is not supported by this script"
    exit 1
fi

# Ask MySQL credentials
new_line
new_line
echo "> About to create 'marketplace' database. Please, provide MySQL credentials with administrative rights (i.e. root user)"
read -p ">> User: " MYSQLUSR
read -s -p ">> Password: " MYSQLPASS

# Create Marketplace Database
mysqladmin -u $MYSQLUSR -p$MYSQLPASS create marketplace

# Unzip WMarket
unzip -q WMarket.war -d WMarket

# Configure Marketplace
sed -i "s|^jdbc.username.*$|jdbc.username=$MYSQLUSR|g" WMarket/WEB-INF/classes/properties/database.properties
sed -i "s|^jdbc.password.*$|jdbc.password=$MYSQLPASS|g" WMarket/WEB-INF/classes/properties/database.properties

# Index
new_line
read -p "> Path to store indexes: " PATH_INDEX

sed -i "s|lucene.IndexPath=.*$|lucene.IndexPath=$PATH_INDEX|g" WMarket/WEB-INF/classes/properties/marketplace.properties

mkdir $PATH_INDEX
chmod a+rw $PATH_INDEX

# Media Files
read -p "> Path to store media files: " PATH_MEDIA
read -p "> Max size for media files (in bytes): " MAX_SIZE_MEDIA_FILES

sed -i "s|^media.folder.*$|media.folder=$PATH_MEDIA|g" WMarket/WEB-INF/classes/properties/marketplace.properties
sed -i "s|^media.maxSize.*$|media.maxSize=$MAX_SIZE_MEDIA_FILES|g" WMarket/WEB-INF/classes/properties/marketplace.properties

mkdir $PATH_MEDIA
chmod a+rw $PATH_MEDIA

# Descriptions Autoupdate
read -p "> Period to update descriptions (in seconds): " PERIOD_UPDATE_DESCRIPTIONS

sed -i "s|^descriptions.updatePeriod.*$|descriptions.updatePeriod=$PERIOD_UPDATE_DESCRIPTIONS|g" WMarket/WEB-INF/classes/properties/marketplace.properties

# OAuth2?
read -e -n 1 -p "> Do you want to use OAuth2 to authenticate users? (Y/n): " OAUTH2

if [ "$OAUTH2" == "Y" ] || [ "$OAUTH2" == "y" ]; then

    # Ask for OAuth2 credentials
    read -p ">> OAuth2 Server: " OAUTH2_SERVER
    read -p ">> OAuth2 Key: " OAUTH2_KEY
    read -p ">> OAuth2 Secret: " OAUTH2_SECRET
    read -p ">> Provider Role: " OAUTH2_PROVIDER_ROLE
    read -p ">> WMarket External IP: " EXTERNAL_IP

    # Replace configuration
    sed -i "s|^oauth2.server.*$|oauth2.server=$OAUTH2_SERVER|g" WMarket/WEB-INF/classes/properties/marketplace.properties
    sed -i "s|^oauth2.key.*$|oauth2.key=$OAUTH2_KEY|g" WMarket/WEB-INF/classes/properties/marketplace.properties
    sed -i "s|^oauth2.secret.*$|oauth2.secret=$OAUTH2_SECRET|g" WMarket/WEB-INF/classes/properties/marketplace.properties
    sed -i "s|^oauth2.providerRole.*$|oauth2.providerRole=$OAUTH2_PROVIDER_ROLE|g" WMarket/WEB-INF/classes/properties/marketplace.properties
    sed -i "s|^oauth2.callbackURL.*$|oauth2.callbackURL=http://$EXTERNAL_IP:8080/WMarket/callback|g" WMarket/WEB-INF/classes/properties/marketplace.properties

    # Enable OAuth2
    sed -i "s|<import resource=\"security.xml\" />|<import resource=\"securityOAuth2.xml\" />|g" WMarket/WEB-INF/classes/spring/config/BeanLocations.xml
fi

# Update war file
cd WMarket
jar uf ../WMarket.war WEB-INF/classes/properties/database.properties WEB-INF/classes/properties/marketplace.properties WEB-INF/classes/spring/config/BeanLocations.xml
cd ..

# Deploy the war file
chmod a+r WMarket.war
cp -p WMarket.war $WEBAPPS_PATH/WMarket.war

# Confirmation message
new_line
new_line
echo -e "> WMarket has been correctly depolyed!!"
