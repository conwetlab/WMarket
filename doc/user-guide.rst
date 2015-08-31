===========================
Users and Programmers Guide
===========================

This guide involves all the processes that can be performed by final users and
programmers. 

This guide is related to the version 4.4.3 of WMarket, released in September 
2015.


-----------
Users Guide
-----------

The users guide is divided into two parts: one related to consumers (users 
interested in acquiring offerings) and another one related to providers (users
interested in acquiring and publishing offerings).

Consumers
=========

Registering and Logging In
--------------------------

The first step to access WMarket is to create a new user account. This proccess
may vary depending on the authentication system selected by the WMarket admin. 

If the WMarket instance you are accessing uses an external authentication
system (i.e. FIWARE IdM), you are not required to create a new user account:
you can use the one existing in the IdM. The first time that you access the
instance, you will be redirected to the IdM where you have to introduce your
credentials. Once that you have introduced your credentials, click **Sign in**
(the button indicated by a blue arrow). If you do not have a FIWARE account
yet, you can create a new one by clicking the **Sign up** button (the one 
indicated the by the green arrow).

.. image:: /images/user-guide/login-idm.png
   :align: center

On the other hand, if you are using the local authentication system, when you
access the instance and you are logged in, a log in dialog will be displayed. 

.. image:: /images/user-guide/login-idm.png
   :align: center

If it is your first time in this instance, you have to create a new account.
To do so, just click the **Sign up** button (the once indicated by the green 
arrow). Otherwise, introduce your credentials (your email and your password)
and click the **Sign in** button (the one indicated by the blue arrow). 

To create a new account, you will be required to introduce some details: your
full name, your email account and a password (that must be reconfirmed). Once
that you have introduced these details, just click **Create**.

.. image:: /images/user-guide/register-user.png
   :align: center

.. note::
  
  When using the local authentication system, you can log in using your email
  or your user name. This user name is based on your full name and can you can
  get it in the profile management page as will be explained below. 

Managing User Profile
---------------------

Once that you are logged in, you will be redirected to the WMarket main page 
where you will be able to find all the provided offerings. To manage your 
profile, just click in your user name on the upper right corner (the button 
indicated by the blue arrow) and then click the **Settings** button (the one 
indicated by the green arrow).

.. image:: /images/user-guide/user_settings_button.png
   :align: center

.. note::
  As can be seen, you can log out by clicking **Log out** button (the one 
  indicated by the black arrow)

When you click on **Settings**, you are redirected to the profile management 
page.

.. image:: /images/user-guide/profile_management.png
   :align: center

If the instance is using the local authentication system, this page will allow
you to:

* Change your personal information
* Become a provider
* Change your password
* Delete your account

If you want to change your personal information, just type your new details
into the **Personal Information** box (the one rounded with a blue rectangle)
and click the **Save Changes** button.

.. note::

  As can be seen, the **Personal Information** box contains your user name. 
  As stated before, you can also use this user name to log in.

You will also be able to become a provider in order to enable new options to 
create stores and publish offerings. To do so, just click the **I want to 
become a provider** button located within the green box. If you are already a 
provider and you just want to be a simple consumer, just click the **I do not
want to be a provider anymore** button.

.. image:: /images/user-guide/become_a_consumer.png
   :align: center

To change your password, click the **Credentials** button (the one indicated by
the black arrow). Then, you will be redirected to a new page that will allow 
you to change your password. In this page, just write your old password and
type twice your new password. If your old password is correct and the new
password fits the password requirements, your password will be changed.

.. image:: /images/user-guide/change_password.png
   :align: center

.. note::

  When you change your password, the system will close your session so you will
  be required to log in again.

Finally, you can also deleted your account. To do so, just click the **Delete
account** button. You will be required to confirm the operation as can be seen
in the following image.

.. image:: /images/user-guide/delete_user_dialog.png
   :align: center

.. note::

  When you delete your account, all your content (reviews, stores, offerings, 
  descriptions...) will be deleted.

Browsing Offerings
------------------

When you access WMarket, you are redirected to the main page. 

.. image:: /images/user-guide/main.png
   :align: center

This page is divided into three parts:

* **Other users are looking at** (red box): shows offerings viewed by other
  users.
* **Last viewed** (blue box): shows the last offerings viewed by you.
* **Categories** (green box): shows offerings divided by their category.
  You can click the name of any category to retrieve the full list of offerings
  contained in this category.

Offering Details
````````````````

You can click any offering to view its details. 

.. image:: /images/user-guide/offering_main.png
   :align: center

This page is divided in different zones:

* The **red box** contains general information about the offering: name, 
  version, description, provider...
* The **blue box** contains the reviews mage by users.
* The **green box** contains all the actions that you can do with the offering:
  view its price plans, view its services or add it you to your bookmarks. 
* The **black box** can be used to review the offering. 

To view all the price plans included in an offering, just click the **Price 
Plans** button. 

.. image:: /images/user-guide/offering_price_plans.png
   :align: center

To view all the services included in an offering, just click the **Services** 
button.

.. image:: /images/user-guide/offering_services.png
   :align: center

Reviewing Offerings
```````````````````

If you want to review an offering, you can easily do it by clicking in one of 
stars contained in the black box. The first star means that you do not like the
offering while the last one means that you really like the offering. You should
click on the appropriate star according to your thoughts. 

Once that you have clicked one star, a new dialog will be opened asking for an
extended review as can be seen in the following picture.

.. image:: /images/user-guide/new_review.png
   :align: center

In this dialog you can change the number of stars and add a brief comment to 
explain your decision. Once that you have completed the review, just click the
**Submit** button. 

Once that you have reviewed an offering, you can edit or delete your review. 
To do so, just click one of the stars contained in the black box. A new dialog
will be opened with your review. 

.. image:: /images/user-guide/new_review.png
   :align: center

To update your previous review, just modify the number of stars and type a new
comment. Then, click the **Save** button (the one indicated by the blue arrow).
On the other hand, if you want to delete your review, just click the **Delete**
button (the one indicated by the red arrow).

Bookmarking
```````````

To add an offering to your bookmarks, just click the **Add bookmark** button
(contained in the green box). The button will change to **Remove bookmark** so
you will be able remove this offering from your bookmarks.

To view the list of bookmarked offerings, you can click the menu button that is
next to the **WMarket** text on the left upper corner as can be seen in the 
following image.

.. image:: /images/user-guide/contextual_menu.png
   :align: center

Then click the **My Bookmarks** button (the one indicated by the blue arrow).
A screen similar to the following one will be displayed.

.. image:: /images/user-guide/bookmark.png
   :align: center

You can click any offering to obtain extended details about the offering.

Comparing Offerings
-------------------

WMarket offers users the option to compare different offerings. To do so, click
the menu button that is next to the **WMarket** text on the left upper corner 
(as can be seen in the following image) and click the **Compare offerings** 
button (the one indicated by the green arrow).

.. image:: /images/user-guide/contextual-menu.png
   :align: center

You will be redirected to a new page that will allow you to create comparisons.

.. image:: /images/user-guide/comparisons.png
   :align: center

To compare offerings, just look for the offerings that you want to compare in
the **Select any offering for comparison** section (the one rounded with a blue
rectangle) and click them. Offerings will appear in the **Compare Offerings** 
section (the one rounded with a green rectangle) as you click them. 

To remove any offering from the comparison, just click on its picture (the
one contained in the red rectangle).

The comparison will show you:

* The description of each offering.
* The categories of each offering. Categories will be ordered and aligned in 
  order to highlight the difference of categories between the compared 
  offerings.
* The price plans of each offering.
* The services of each offering. Services will be ordered and aligned in order
  to highlight the difference of services between the different offerings.