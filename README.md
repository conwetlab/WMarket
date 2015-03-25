WMarket
=======
WMarket is the reference implementation of the FIWARE Marketplace Generic Enabler. The Marketplace provides functionality necessary for bringing together offering and demand for making business. These functions include basic services for registering business entities, publishing and retrieving offerings and demands, search and discover offerings according to specific consumer requirements as well as lateral functions like review, rating and recommendation. Besides the core functions, the Marketplace may offer value because of its "knowledge" about the market in terms of market intelligence services, pricing support, advertising, information subscription and more.

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
Here you have a basic reference of all the status codes that you can get when you are dealing with WMarket API:

| HTTP Code | Type | Description |
|-----------|------|------------ |
| 200 | OK   | Your request has been properly completed |
| 201 | Created | Your resource has been created. The `Location` header will contain the final URL of the new created resource |
| 204 | Deleted | Your resource has been properly deleted |
| 400 | Bad Request | The content of your request is not correct (e.g. there is already a resource with the specified name) |
| 400 | Validation Error | One or more fields of your content is not valid. The field `field` indicates the first field with a validation error |
| 403 | Forbidden | You have no rights to perform the query |
| 500 | Internal server error | There was an internal error in the system so your request cannot be completed |

### Users API

#### Create a user

* **Path**: `/api/v2/user`
* **Method**: POST
* **Accept**: `application/json` or `application/xml`
* **Body**:
```
{
  "displayName": "Example Display Name",
  "email": "example@example.com",
  "password": "example_complex_password",
  "company": "Example Company"
}
```

#### Update a user

* **Path**: `/api/v2/user/{USER_NAME}`
* **Method**: POST
* **Accept**: `application/json` or `application/xml`
* **Body**:
```
{
  "displayName": "Example Display Name",
  "email": "example@example.com",
  "password": "example_complex_password",
  "company": "Example Company"
}
```

#### Update a user

* **Path**: `/api/v2/user/{USER_NAME}`
* **Method**: DELETE

#### Get a user

* **Path**: `/api/v2/user/{USER_NAME}`
* **Method**: GET
* **Content-Type**: `application/json` or `application/xml`
* **Content**:
```
{
  "displayName": "Example Display Name",
  "registrationDate": 1,
  "company": "Example Company"
}
```

#### List of users

* **Path**: `/api/v2/user`
* **Method**: GET
* **Content-Type**: `application/json` or `application/xml`
* **Params**:
 * **limit**: The amount of elements to be retrieved
 * **offset**: The first element to be retrieved
* **Content**:
```
[ 
  {
    "displayName": "Example Display Name",
    "registrationDate": 1,
    "company": "Example Company"
  },
  {
    "displayName": "Example Display Name 2",
    "registrationDate": 2,
    "company": "Example Company 2"
  },
  [...]
]
```
