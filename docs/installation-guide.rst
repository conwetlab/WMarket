==================
Installation Guide
==================

The purpose of this document is to describe how to install and
administrate the necessary software on a server so that it can run
WMarket.

WMarket itself is a Java Web Application, packaged in a WAR file and
relies on a SQL database.

-------------------
System Requirements
-------------------

This section covers the requirements needed to install and use WMarket.

Hardware Requirements
=====================

The following table contains the minimum resource requirements for
running WMarket:

+----------------+--------------------------------------------------------------------------------------------+
| Resource       | Requirement                                                                                |
+================+============================================================================================+
| CPU            | 1-2 cores with at leas 2.4 GHz                                                             |
+----------------+--------------------------------------------------------------------------------------------+
| Physical RAM   | 2GB-4GB                                                                                    |
+----------------+--------------------------------------------------------------------------------------------+
| Disk Space     | 10GB The actual disk space will depend on the amount of data being stored within WMarket   |
+----------------+--------------------------------------------------------------------------------------------+

Operating System Support
========================

WMarket has been tested against the following Operating Systems: 

- Ubuntu 14.04 LTS 
- CentOS 7

However, you can install WMarket in any machine where you can install an
Application Server (such as Tomcat) and a MySQL server.

.. note::
  This Installation Guide describes the installation process on a
  Linux based System.

Software Requirements
=====================

In order to have WMarket running, the following software is needed:

-  MySQL Server 5.5
-  Java 1.7.x
-  Application Server, Apache Tomcat 7.x
-  WMarket itself

---------------------
Software Installation
---------------------

Before you can use WMarket on your own machine(s), you need to install it. 
There are three ways to install WMarket:

#. :ref:`With the Installation Script <install_wmarket_script>`
#. :ref:`With a Docker Image <install_wmarket_docker>`
#. :ref:`Manual <install_wmarket_manual>`

.. _install_wmarket_script:

Installing WMarket using scripts
================================

In order to ease the WMarket installation, the script ``install.sh`` can be 
used. This script downloads the latest stable version of WMarket (from GitHub)
if the file `WMarket.war` does not exist, install all the needed dependencies,
configures the instance (database and preferences) and deploys it in the Tomcat 
Server. You can find this script 
`here <https://raw.githubusercontent.com/conwetlab/WMarket/master/utils/install.sh>`_. 

Once that you have downloaded the script, you can easily run it by executing 
the following command:

::

    $ sudo ./install.sh

.. note::
  If you use this script you can skip the configuration section;
  however, it is highly recommended to read it in order to understand the
  different configuration parameters.

During the execution of the script you will be prompted several times in
order to retrieve some information. Specifically:

-  **Database Installation**: in Debian/Ubuntu systems the MySQL installer
   automatically ask a password to create the root user. In CentOS systems the 
   the script calls the MySQL configuration command in order to create this
   password so a message similar to the following one will be shown:

::

    NOTE: RUNNING ALL PARTS OF THIS SCRIPT IS RECOMMENDED FOR ALL MySQL
          SERVERS IN PRODUCTION USE!  PLEASE READ EACH STEP CAREFULLY!

    In order to log into MySQL to secure it, we'll need the current
    password for the root user.  If you've just installed MySQL, and
    you haven't set the root password yet, the password will be blank,
    so you should just press enter here.

    Enter current password for root (enter for none): 
    OK, successfully used password, moving on...

    Setting the root password ensures that nobody can log into the MySQL
    root user without the proper authorisation.

    Set root password? [Y/n] y  
    New password: *******
    Re-enter new password: *******
    Password updated successfully!
    Reloading privilege tables..
     ... Success!


    By default, a MySQL installation has an anonymous user, allowing anyone
    to log into MySQL without having to have a user account created for
    them.  This is intended only for testing, and to make the installation
    go a bit smoother.  You should remove them before moving into a
    production environment.

    Remove anonymous users? [Y/n] y
     ... Success!

    Normally, root should only be allowed to connect from 'localhost'.  This
    ensures that someone cannot guess at the root password from the network.

    Disallow root login remotely? [Y/n] y
     ... Success!

    By default, MySQL comes with a database named 'test' that anyone can
    access.  This is also intended only for testing, and should be removed
    before moving into a production environment.

    Remove test database and access to it? [Y/n] y
     - Dropping test database...
    ERROR 1008 (HY000) at line 1: Can't drop database 'test'; database doesn't exist
     ... Failed!  Not critical, keep moving...
     - Removing privileges on test database...
     ... Success!

    Reloading the privilege tables will ensure that all changes made so far
    will take effect immediately.

    Reload privilege tables now? [Y/n] y
     ... Success!




    All done!  If you've completed all of the above steps, your MySQL
    installation should now be secure.

    Thanks for using MySQL!


    Cleaning up...

-  **Database Configuration**: the script creates a database called
   ``marketplace``. To perform this action, MySQL credentials with 
   administrative permission are required. For this reason, you will see the
   following lines:

::

    > About to create 'marketplace' database. Please, provide MySQL credentials with administrative rights (i.e. root user)
    >> User: root
    >> Password: *******

-  **Index path**: WMarket uses Lucene indexes to provide better search 
   results. These indexes have to be stored in files so the script will ask 
   you for a folder to store them.

::

      > Path to store indexes: [FOLDER_TO_STORE_INDEXES]

-  **Media files**: WMarket users will be able to attach images to stores in
   order to improve the navigation. These images have to be saved in a folder
   of the file system used by the WMarket instance. For this reason, the script
   will ask you for a path to store these images and their maximum size:

::

      > Path to store media files: [FOLDER_TO_STORE_MEDIA]
      > Max size for media files (in bytes): [MAX_FILE_SIZE]

-  **Description Autoupdate**: uploaded descriptions are regularly checked to
   verify if new offerings have been included. The script will ask you the 
   period that have to be used to check these descriptions.

::

      > Period to update descriptions (in seconds): [PERIOD_TO_UPDATE_DESCRIPTIONS]

-  **Authentication Configuration**: WMarket can work with a local
   authentication system or using an external IdM. The script will ask you what
   authentication system you prefer. If you opt for an external IdM,
   some configuration details will be required as can be seen in the
   following example:

::

    > Do you want to use OAuth2 to authenticate users? (Y/n): Y
    >> OAuth2 Server: [FIWARE_IDM_URL]
    >> OAuth2 Key: [OAUTH2_KEY]
    >> OAuth2 Secret: [OAUTH2_SECRET]
    >> Provider Role: [OAUTH2_PROVIDER_ROLE]
    >> WMarket External IP: [MACHINE_IP]

This script will directly configure some preferences with default values
(see the :ref:`Configuration <wmarket_configuration>` section for more info of
the described preferences). Concretely:

-  A database called ``marketplace`` is created.
-  Database connection is configured with the administrative credentials
   you have provided.
-  Two new folders are created: one for index files and another for media 
   files. The permissions of these folders are changed so Tomcat can access
   them.
-  Authentication system is configured according to your preferences.
-  The Marketplace is deployed in Tomcat as ``WMarket``.

.. _install_wmarket_docker:

Installing WMarket using Docker
===============================

Stating on version 4.3.3, you are able to run WMarket with Docker. As you may 
know, WMarket needs a MySQL database to store some information. For this 
reason, you must create an additional container to run the database. You can do 
it automatically with ``docker-compose`` or manually by following the given 
steps.

The WMarket image is available on `Docker Hub <https://hub.docker.com/r/conwetlab/wmarket/>`_.

Using docker-componse
---------------------

You can install WMarket automatically if you have ``docker-compose`` installed
in your machine. To do so, you must create a folder to place a new file called 
``docker-compose.yml`` that should include the following content:

::

    wmarket_db:
        restart: always
        image: mysql:latest
        volumes:
             - /var/lib/mysql
        environment:
            - MYSQL_ROOT_PASSWORD=my-secret-pw
            - MYSQL_DATABASE=marketplace

    wmarket:
        restart: always
        image: conwetlab/wmarket
        volumes:
            - /WMarket/static
        ports:
            - "80:8080"
        links:
            - wmarket_db

Once that you have created the file, run the following command:

::

    docker-compose up

Then, WMarket should be up and running in ``http://YOUR_HOST:80/WMarket`` 
replacing ``YOUR_HOST`` by the host of your machine.

Without docker-compose
----------------------

1) Creating a Container to host the Database
````````````````````````````````````````````

The first thing that you have to do is to create a docker container that will 
host the database used by WMarket. To do so, you can execute the following 
command:

::

    docker run --name wmarket_db -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=marketplace -v /var/lib/mysql -d mysql

* As can be seen, some environment variables are set in this command to set up 
  the data base. You must **not** change these variables, since their values 
  are the ones expected by the WMarket image.

2) Deploying the WMarket Image
``````````````````````````````

Once that the database is configured, you can deploy the image by running the 
following command (*replace* ``PORT`` *by the port of your local machine that 
will be used to access the service*):

::

    docker run --name wmarket -v /WMarket/static -p PORT:8080 --link wmarket_db -d conwetlab/wmarket

Once that you have run these commands, WMarket should be up and running in 
``http://YOUR_HOST:PORT/WMarket`` replacing ``YOUR_HOST`` by the host of your 
machine and ``PORT`` by the port selected in the previous step. 

.. _install_wmarket_manual:

Manually installing WMarket
===========================

All the mandatory dependencies can be easily installed on a Debian based
Linux distribution using ``apt-get``:

**Ubuntu/Debian:**

::

    sudo apt-get install mysql-server mysql-client
    sudo apt-get install openjdk-7-jdk
    sudo apt-get install tomcat7 tomcat7-docs tomcat7-admin

**CentOS/RedHat:**

In a CentOS/RedHat system, all the required dependencies can be
installed using ``yum``:

::

    # Install MySQL
    sudo rpm -Uvh http://dev.mysql.com/get/mysql-community-release-el7-5.noarch.rpm
    sudo yum -y install mysql-community-server

    # Install Java
    sudo yum -y install java-1.7.0-openjdk-devel

    # Install Tomcat 7
    sudo yum -y install tomcat tomcat-webapps tomcat-admin-webapps

Configuration
-------------

This section explains how to create WMarket database and how to
configure the different preferences. Note that if you have used the
provided script you can just skip this section. However, it is highly
recommended to read it in order to understand the different settings.

Database Configuration
``````````````````````

WMarket uses an internal database named ``marketplace`` that need to be
created in MySQL. To create it you need to have administrator
permissions in MySQL. This usually means that you have to use the MySQL
root user with the password you chose during the installation process.

**Ubuntu/Debian**

::

    sudo service mysql start
    mysqladmin -u root -p[MYSQL_ROOT_PWD] create marketplace

**CentOS/RedHat:**

::

    sudo systemctl start mysqld
    mysqladmin -u root -p[MYSQL_ROOT_PWD] create marketplace

.. note::
  In CentOS, MySQL is installed without requiring any password for the root
  user. The default password is empty. If you want to set up a password for the
  root user, you can run the ``/usr/bin/mysql_secure_installation`` script.

.. _wmarket_configuration:

WMarket Configuration
`````````````````````

Before deploying the provider JAR into your Application Server, you must
configure some parameters.

Database
''''''''

Before you deploy WMarket, you have to set up the database. To do so,
you have to edit the file
``WMarket.war/WEB-INF/classes/properties/database.properties`` and
complete the fields as follows:

::

    jdbc.driverClassName=com.mysql.jdbc.Driver
    jdbc.url=jdbc:mysql://[MYSQL_SERVER]:[MYSQL_PORT]/marketplace
    jdbc.username=[YOUR_DB_USER]
    jdbc.password=[YOUR_DB_PASSWORD]

Index
'''''

WMarket uses index files to provide better search results. These files
must be stored in some folder of your file system. You can specify this
folder by editing the property ``lucene.IndexPath`` included in the file
``WMarket.war/WEB-INF/classes/properties/marketplace.properties``.

.. note::
  Ensure that tomcat user can read and write new files in this directory.

Media Files
'''''''''''

Under certain circumstances, users are allowed to upload media files (images)
in order to ease the process of identifying assets. You can edit the file
``WMarket.war/WEB-INF/classes/properties/marketplace.properties`` to set where 
these files are stored and their maximum size:

::

    # Static files
    media.folder=[FOLDER_TO_STORE_MEDIA_FILES]
    media.maxSize=[MAX_FILE_SIZE_IN_BYTES]

.. note::
  Ensure that tomcat user can read and write new files in this directory.

Descriptions Autoupdate
'''''''''''''''''''''''

Descriptions are the files used to define the offerings that will be available
in WMarket. These files are parsed when they are uploaded to WMarket but 
when they are updated, there is no way to automatically reflect these 
changes. For this reason, you can set an interval to update all the 
descriptions and the offerings that they contain. To do it, set the preference 
``descriptions.updatePeriod`` (included in the file 
``WMarket.war/WEB-INF/classes/properties/marketplace.properties``) with the 
period that should be used to update the descriptions. The value must be 
written in seconds.

OAuth2
''''''

WMarket uses a local authentication system by default. However, the
software is ready to work with the FIWARE Identity Manager.

The first thing that you have to do is to create a new application in
the FIWARE IdM. To do so, go to
https://account.lab.fiware.org/idm/myApplications/create/ and complete
all the required fields:

-  **Name**: *You can choose any name*
-  **Description**: *You can write any description*
-  **URL**: ``http://[WMARKET_HOST]:[WMARKET_PORT]``
-  **Callback URL**:
   ``http://[WMARKET_HOST]:[WMARKET_PORT]/WMarket/callback?client_name=FIWAREClient``
-  **Roles**: In order to allow some users to create stores and
   descriptions with the user interface, you must create a new role. You
   can choose any name for this role.

Once that you have created the application in the FIWARE IdM, you must
edit the following configuration files:

1. ``WMarket.war/WEB-INF/classes/spring/config/BeanLocations.xml``:
   replace ``<import resource="security.xml" />`` by
   ``<import resource="securityOAuth2.xml" />``.
2. ``WMarket.war/WEB-INF/classes/properties/marketplace.properties``:
   set up your OAuth2 following the next template:

::

    # OAuth2
    oauth2.server=[OAUTH2_SERVER]
    oauth2.key=[OAUTH2_KEY]
    oauth2.secret=[OAUTH2_SECRET]
    oauth2.callbackURL=http://[WMARKET_SERVER]:[WMARKET_PORT]/WMarket/callback
    oauth2.signOutPath=auth/logout
    oauth2.providerRole=[OFFERING_PROVIDER_ROLE]

WMarket Deployment
------------------

WMarket can now be installed by copying the WAR file into the
``webapps`` folder of Apache Tomcat. If you have installed Tomcat using
the package manager, the ``webapps`` folder should be located at: 

* In Ubuntu/Debian: ``/var/lib/tomcat7/webapps`` 
* In CentOS/Redhat: ``/usr/share/tomcat/webapps``

To install WMarket on other Java Application Servers (e.g. JBoss),
please refer to their specific application server guidelines.

Once that you have copied the WAR file into the ``webapps`` folder, you
can start Tomcat. The way to do it depends on your operating system.

**Ubuntu/Debian:**

::

    sudo service tomcat7 start

**CentOS/Redhat:**

::

    sudo systemctl start tomcat


-----------------------
Sanity check procedures
-----------------------

The Sanity Check Procedures are those activities that a System
Administrator has to perform to verify that an installation is ready to
be tested. Therefore there is a preliminary set of tests to ensure that
obvious or basic malfunctioning is fixed before proceeding to unit
tests, integration tests and user validation.

End to End testing
==================

Although one End to End testing must be associated to the Integration
Test, we can show here a quick testing to check that everything is up
and running. The first test step involves registering a new user. The
second test step tests if it is possible to authenticate against the
WMarket.

**Step 1: Registering a new user**

.. note::
  If you have chosen an external authentication system (i.e.
  FIWARE IdM), you can avoid this step.

Go to ``http://[WMARKET_HOST]:[WMARKET_PORT]/WMarket/register`` and
complete all the fields appropriately. After clicking "Create", a
confirmation message should inform you that the user has been created
correctly. If you receive an error check that you have complete all the
fields in an appropriate way and that you have follow all the
instructions given in this guide.

**Step 2: Authenticating your user**

Go to \`\ ``http://[WMARKET_HOST]:[WMARKET_PORT]/`` and introduce the
authentication details according to the user that you have created in
the previous step. If you don't obtain any error, the WMarket is
correctly deployed. Congratulations!!

List of Running Processes
=========================

You can execute the command ``ps -ax | grep 'tomcat\|mysql'`` to check
that the Tomcat web server and the MySQL database are running. It should
show a message text similar to the following:

::

      846 ?        Sl    60:40 /usr/bin/java -Djava.util.logging.config.file=/root/tomcat8/conf/logging.properties -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.endorsed.dirs=/root/tomcat8/endorsed -classpath /root/tomcat8/bin/bootstrap.jar:/root/tomcat8/bin/tomcat-juli.jar -Dcatalina.base=/root/tomcat8 -Dcatalina.home=/root/tomcat8 -Djava.io.tmpdir=/root/tomcat8/temp org.apache.catalina.startup.Bootstrap start
      911 ?        Ssl   17:24 /usr/sbin/mysqld

Network interfaces Up & Open
============================

To check whether the ports in use are listening, execute the command
``netstat -ntpl``. The expected results must be somehow similar to the
following:

::

    Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
    tcp        0      0 127.0.0.1:3306          0.0.0.0:*               LISTEN      911/mysqld      
    tcp6       0      0 :::443                  :::*                    LISTEN      846/java        
    tcp6       0      0 :::8009                 :::*                    LISTEN      846/java
    tcp6       0      0 :::80                   :::*                    LISTEN      846/java        

Databases
=========

The last step in the sanity check (once that we have identified the
processes and ports) is to check the database that has to be up and
accept queries. For that, we execute the following commands:

::

    $ mysql -u [DB_USER] -p[DB_PASS] marketplace
    > show tables;

It should show a message text similar to the following:

::

    +-----------------------+
    | Tables_in_marketplace |
    +-----------------------+
    | bookmarks             |
    | categories            |
    | descriptions          |
    | last_viewed           |
    | offerings             |
    | offerings_categories  |
    | offerings_services    |
    | price_components      |
    | price_plans           |
    | reviewable_entity     |
    | reviews               |
    | services              |
    | services_categories   |
    | stores                |
    | users                 |
    +-----------------------+
    15 rows in set (0.00 sec)


--------------------
Diagnosis Procedures
--------------------

The Diagnosis Procedures are the first steps that a System Administrator
has to take to locate the source of an error in a GE. Once the nature of
the error is identified by these tests, the system admin can resort to
more concrete and specific testing to pinpoint the exact point of error
and a possible solution.

Resource availability
=====================

The resource load of the WMarket strongly depends on the number of
concurrent requests received as well as on the free main memory and disk
space:

-  Mimimum available main memory: 256 MB
-  Mimimum available hard disk space: 2 GB

Resource consumption
====================

Resource consumption strongly depends on the load, especially on the
number of concurrent requests.

-  The main memory consumption of the Tomcat application server should
   be between 48MB and 1024MB. These numbers can vary significantly if
   you use a different application server.

I/O flows
=========

The only expected I/O flow is of type HTTP or HTTPS, on ports defined in
Apache Tomcat configuration files, inbound and outbound. Requests
interactivity should be low.
