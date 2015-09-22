FROM tomcat:8-jre8

MAINTAINER "Aitor Magán <amagan@conwet.com>"

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
ENV WMARKET_VERSION v4.3.3
ENV WMARKET_FILE WMarket.war

WORKDIR /wmarket

# Needed packages
RUN apt-get update && apt-get install -y zip

# Get the WAR, configure the Database and create the new WAR
RUN wget https://github.com/conwetlab/WMarket/releases/download/$WMARKET_VERSION/$WMARKET_FILE
RUN unzip $WMARKET_FILE -d WMarket
RUN rm $WMARKET_FILE
RUN sed -i 's/jdbc.url=jdbc:mysql:\/\/localhost:3306\/marketplace/jdbc.url=jdbc:mysql:\/\/wmarket_db:3306\/marketplace/g' WMarket/WEB-INF/classes/properties/database.properties
RUN sed -i 's/jdbc.password=/jdbc.password=my-secret-pw/g' WMarket/WEB-INF/classes/properties/database.properties
RUN sed -i 's/media.folder=static/media.folder=\/WMarket\/static/g' WMarket/WEB-INF/classes/properties/marketplace.properties
RUN cd WMarket && zip -r ../$WMARKET_FILE *

# Upload the application to Tomcat so it will run
RUN cp $WMARKET_FILE /usr/local/tomcat/webapps
