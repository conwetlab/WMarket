====================
Administration Guide
====================

The WMarket Graphical User Interface (GUI) can be very complex for consumers
(those users who do not create stores or description/offerings). For this
reason, options to create stores and descriptions are hidden by default. 

The proccess to become a provider is very easy and depends on the 
selected authentication system. 


----------------------------------
External Authentication (i.e. IdM)
----------------------------------

If your WMarket instance is using an IdM (i.e. FIWARE) to authenticate users,
you have to create a role and assign this role to those users who are 
interested in publishing descriptions/offerings. To do so, the first thing that
you have to do is to access the IdM. Then, identify the WMarket application
among all the available applications. You will be redirected to the application
management page where you have to click the **Manage Roles** button.

.. image:: /images/administration-guide/manage_roles_button.png
   :align: center

Once that you are in the page to manage roles, you have to create a new one. To
do so, click the **+** button as shown in the following image.

.. image:: /images/administration-guide/create_new_role_button.png
   :align: center

Then, type the name of the new role. You can choose any name.

.. image:: /images/administration-guide/create_new_role_dialog.png
   :align: center

When the new role is created, you have to modify the WMarket preferences so it
can understand which users are providers. To do so, you have to edit the file
``WMarket.war/WEB-INF/classes/properties/marketplace.properties`` and modify
the ``oauth2.providerRole`` preference, where you have to set the name of the
role that you have created in the last step.

Finally, you have to assign the role to those users who want to become
providers. To achieve this, access the IdM and click on the WMarket
application. Then, click the **Authorize** button as shown in the following
image. 

.. image:: /images/administration-guide/authorize-button.png
   :align: center

A new dialog will be opened. Just write the name of the user to which you want
to assing the provider role. Then, click the **+** button.

.. image:: /images/administration-guide/look_for_user.png
   :align: center

The user will be moved to the **Authorized users** section. Finally, click on
the **No Roles** button and assign the provider role as displayed in this
image.

.. image:: /images/administration-guide/assing_provider_role.png
   :align: center

Then click **Save**.

.. note::
  These changes will only take effect when the user logins in again. 


--------------------
Local Authentication
--------------------

If the WMarket instance you are administering is based on a local 
authentication system, you do not have to worry about upgrading users to
providers since they can do it by themselves in a very easy way by editing
their prefereces. To do so, users should click on their name and then
click the **Settings** button.

.. image:: /images/administration-guide/user_settings_button.png
   :align: center

After this they will be redirected to the user settings management page where
a button to become provider can be found. 

.. image:: /images/administration-guide/user_settings_become_provider.png
   :align: center

