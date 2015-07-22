WMarket
=======
WMarket is the reference implementation of the FIWARE Marketplace Generic Enabler. The Marketplace provides functionality necessary for bringing together offering and demand for making business. These functions include basic services for registering business entities, publishing and retrieving offerings and demands, search and discover offerings according to specific consumer requirements as well as lateral functions like review, rating and recommendation. Besides the core functions, the Marketplace may offer value because of its "knowledge" about the market in terms of market intelligence services, pricing support, advertising, information subscription and more.

This project is part of [FIWARE](http://www.fiware.org). Check it out in the [Catalogue](http://catalogue.fiware.org/enablers/marketplace-wmarket)!

Prerequisites
-------------
For running WMarket in your system, you need to install the following requisites. You have to install them according to your system:
* Tomcat 8
* MySQL
* Java 8
* Maven

Installation
------------
You can install WMarket by following these steps:

1. Install the prerequisites
2. Create a database for the MarketPlace. By default, WMarket uses the database `marketplace`. 
3. Update `src/main/resources/properties/database.properties` to set the preferences of your database.
4. Update `src/main/resources/properties/marketplace.properties` according to your preferences.
5. Configure the security
 1. If you want to use the FIWARE IdM to manage the users, ensure that the file `securityOAuth2.xml` is imported in the beans location file (`src/main/resources/spring/config/BeansLocation.xml`) and modify `src/main/resources/properties/marketplace.properties` to set your OAuth2 configuration
 2. If you want to use local authorization, ensure that the file `security.xml` is imported in the beans location file (`src/main/resources/spring/config/BeansLocation.xml`).
6. Run `mvn package` to generate the WAR file.
7. Copy the generated WAR file into the `webapps` folder of your Tomcat instance.

API Reference
-------------
You can check the API Reference on [Apiary](http://docs.fiwaremarketplace.apiary.io)
