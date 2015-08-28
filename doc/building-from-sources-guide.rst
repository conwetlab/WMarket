=====================
Building from Sources
=====================

This document explains how a user can obtain the WMarket sources to compile it
and generate a customized WAR that can be run in an Application Server (i.e. 
Tomcat,...)

.. note::

  This document is intended for Java developers with experience in developing
  their own Web Applications


-------------------
System Requirements
-------------------

To compile WMarket you are required to install the following software:

* Git: to obtain the WMarket Source Code
* Maven 3: to compile the source code

You can easily install these programs by using the package manager software
offered by your operating system.


-------------------------
Obtaining the Source Code
-------------------------

Once that you have installed the minimun requirements, you are able to obtain
the source code. To do it, execute the following command:

::

    $ git clone git@github.com:conwetlab/WMarket.git


-------------------
Configuring WMarket
-------------------

Before compiling the code, you can be interested in editing some preferences to
customize your WMarket instance. In this way, you will be able to:

* Modify the database used to store the information
* Modify the folder where the indexes are stored
* Select the authorization system (local or external (i.e. IdM))

You can find a full explanation of all these configurations at 
:ref:`WMarket Configuration <wmarket_configuration>`


-------------------------
Compiling the Source Code
-------------------------

To compile the code, you can execute the following command:

:: 

    $ mvn install

.. note::
  
  You can execute the command with the argument ``-DskipTests`` so the tests
  will not be executed and the WAR will be generated faster.


This will generate a new folder called ``target`` that will contain the WAR
that you can deploy on your Application Server. This WAR is called 
``WMarket.war``


-------------------
Executing the tests
-------------------

WMarket is provided with unit tests and integration tests. You can run the unit
tests by executing this command:

::

    $ mvn test

If you want to run all the tests (unit and integration) you must run the 
following command:

::

    $ mvn integration-test