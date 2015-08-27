Installation and Administration Guide
=====================================

The purpose of this document is to describe how to install and
administrate the necessary software on a server so that it can run
WMarket.

WMarket itself is a Java Web Application, packaged in a WAR file and
relys on a SQL database.

System Requirements
===================

This section covers the requirements needed to install and use WMarket.

Hardware Requirements
---------------------

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
------------------------

WMarket has been tested against the following Operating Systems: 

- Ubuntu 14.04 LTS 
- CentOS 7

However, you can install WMarket in any machine where you can install an
Application Server (such as Tomcat) and a MySQL server.

.. note::

  This Installation Guide describes the installation process on a
  Linux based System.

Software Requirements
---------------------

In order to have WMarket running, the following software is needed:

-  MySQL Server 5.5
-  Java 1.7.x
-  Application Server, Apache Tomcat 7.x
-  WMarket itself

Software Installation
---------------------

Installing WMarket using scripts
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In order to ease the WMarket installation, the script ``install.sh`` is
provided. This script installs all the needed dependencies, configures
the WMarket instance (database and preferences) and deploys it in the
Tomcat Server. To use this script execute the command:

::

    $ sudo ./install.sh

.. note::
  If you use this script you can skip the configuration section;
  however, it is highly recommended to read it in order to understand the
  different configuration parameters.

During the execution of the script you will be prompted several times in
order to retrieve some information. Specifically:

-  **Database Installation**: in Debian/Ubuntu systems the MySQL
   installer automatically ask a password for the creation of the root
   user. In CentOS systems the script calls the MySQL configuration
   command in order to create this password so a message similar to the
   following one will be shown:

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
   ``marketplace``. To perfom this action, the script needs MySQL
   credentials with administrative permission. For this reason, you will
   see the following lines:

::

    > About to create 'marketplace' database. Please, provide MySQL credentials with administrative rights (i.e. root user)
    >> User: root
    >> Password: *******

-  **Authentication Configuration**: WMarket can work with local
   authentication or using an external IdM. The script will ask you what
   authentication system do you prefer. If you opt for an external IdM,
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
(see the *Configuration* section for more info of the described
preferences). Concretely:

-  A database called ``marketplace`` is created.
-  Database connection is configured with the administrative credentials
   you have provided.
-  Authentication system is configured according to your preferences.
-  The Marketplace is deployed in Tomcat as ``WMarket``.

Manually installing WMarket
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Ubuntu/Debian
^^^^^^^^^^^^^

All the mandatory dependencies can be easily installed on a Debian based
Linux distribution using ``apt-get``:

::

    sudo apt-get install mysql-server mysql-client
    sudo apt-get install openjdk-7-jdk
    sudo apt-get install tomcat7 tomcat7-docs tomcat7-admin

CentOS/RedHat
^^^^^^^^^^^^^

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
~~~~~~~~~~~~~~~~~~~~~~

WMarket uses an internal database named ``marketplace`` that need to be
created in MySQL. To create it you need to have administrator
permissions in MySQL. This usually means that you have to use the MySQL
root user with the password you chose during the installation process.

Ubuntu/Debian:
^^^^^^^^^^^^^^

::

    sudo service mysql start
    mysqladmin -u root -p[MYSQL_ROOT_PWD] create marketplace

CentOS/RedHat:
^^^^^^^^^^^^^^

::

    sudo systemctl start mysqld
    mysqladmin -u root -p[MYSQL_ROOT_PWD] create marketplace

WMarket Configuration
~~~~~~~~~~~~~~~~~~~~~

Before deploying the provider JAR into your Application Server, you must
configure some parameters.

Database
^^^^^^^^

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
^^^^^

WMarket uses index files to provide better search results. These files
must me stored in some folder of your file system. You can specify this
folder by editing the property ``lucene.IndexPath`` included in the file
``WMarket.war/WEB-INF/classes/properties/marketplace.properties``.

OAuth2
^^^^^^

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
~~~~~~~~~~~~~~~~~~

WMarket can now be installed by copying the WAR file into the
``webapps`` folder of Apache Tomcat. If you have installed Tomcat using
the package manager, the ``webapps`` folder should be located at: 

* In Ubuntu/Debian: ``/var/lib/tomcat7/webapps`` 
* In CentOS/Redhat: ``/usr/share/tomcat/webapps``

To install WMarket on other Java Application Servers (e.g. JBoss),
please refer to their specific application server guidelines.

Once that you have copied the WAR file into the ``webapps`` folder, you
can start Tomcat. The way to do it depends on your operating system.

Ubuntu/Debian:
^^^^^^^^^^^^^^

::

    sudo service tomcat7 start

CentOS/Redhat:
^^^^^^^^^^^^^^

::

    sudo systemctl start tomcat

Sanity check procedures
=======================

The Sanity Check Procedures are those activities that a System
Administrator has to perform to verify that an installation is ready to
be tested. Therefore there is a preliminary set of tests to ensure that
obvious or basic malfunctioning is fixed before proceeding to unit
tests, integration tests and user validation.

End to End testing
------------------

Although one End to End testing must be associated to the Integration
Test, we can show here a quick testing to check that everything is up
and running. The first test step involves registering a new user. The
second test step tests if its possible to authenticate against the
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
-------------------------

You can execute the command ``ps -ax | grep 'tomcat\|mysql'`` to check
that the Tomcat web server and the MySQL database are running. It should
show a message text similar to the following:

::

      846 ?        Sl    60:40 /usr/bin/java -Djava.util.logging.config.file=/root/tomcat8/conf/logging.properties -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.endorsed.dirs=/root/tomcat8/endorsed -classpath /root/tomcat8/bin/bootstrap.jar:/root/tomcat8/bin/tomcat-juli.jar -Dcatalina.base=/root/tomcat8 -Dcatalina.home=/root/tomcat8 -Djava.io.tmpdir=/root/tomcat8/temp org.apache.catalina.startup.Bootstrap start
      911 ?        Ssl   17:24 /usr/sbin/mysqld

Network interfaces Up & Open
----------------------------

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
---------

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

Diagnosis Procedures
====================

The Diagnosis Procedures are the first steps that a System Administrator
has to take to locate the source of an error in a GE. Once the nature of
the error is identified by these tests, the system admin can resort to
more concrete and specific testing to pinpoint the exact point of error
and a possible solution.

Resource availability
---------------------

The resource load of the WMarket strongly depends on the number of
concurrent requests received as well as on the free main memory and disk
space:

-  Mimimum available main memory: 256 MB
-  Mimimum available hard disk space: 2 GB

Resource consumption
--------------------

Resource consumption strongly depends on the load, especially on the
number of concurrent requests.

-  The main memory consumption of the Tomcat application server should
   be between 48MB and 1024MB. These numbers can vary significantly if
   you use a different application server.

I/O flows
---------

The only expected I/O flow is of type HTTP or HTTPS, on ports defined in
Apache Tomcat configuration files, inbound and outbound. Requests
interactivity should be low.
