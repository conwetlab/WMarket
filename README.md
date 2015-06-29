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

### Users Management API

#### Create a user

* **Path**: `/api/v2/user`
* **Method**: POST
* **Content-Type**: `application/json` or `application/xml`
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
* **Content-Type**: `application/json` or `application/xml`
* **Body**:
```
{
  "displayName": "Example Display Name",
  "email": "example@example.com",
  "password": "example_complex_password",
  "company": "Example Company"
}
```

#### Delete a user

* **Path**: `/api/v2/user/{USER_NAME}`
* **Method**: DELETE

#### Get a user

* **Path**: `/api/v2/user/{USER_NAME}`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Content**:
```
{
  "userName": "example-display-name",
  "displayName": "Example Display Name",
  "createdAt": 1,
  "company": "Example Company"
}
```

#### List users

* **Path**: `/api/v2/user`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **limit**: The amount of elements to be retrieved
 * **offset**: The first element to be retrieved
* **Content**:
```
[ 
  {
    "userName": "example-display-name",
    "displayName": "Example Display Name",
    "createdAt": 1,
    "company": "Example Company"
  },
  {
    "userName": "example-display-name-2",
    "displayName": "Example Display Name 2",
    "createdAt": 2,
    "company": "Example Company 2"
  },
  [...]
}
```

### Stores Management API

#### Create a store

* **Path**: `/api/v2/store`
* **Method**: POST
* **Content-Type**: `application/json` or `application/xml`
* **Body**:
```
{
  "displayName": "Example Store",
  "url": "https://store.lab.fiware.org",
  "comment": "Example comment",
  "imageBase64": "PNG_IMAGE_IN_BASE64"
}
```

#### Update a store

* **Path**: `/api/v2/store/{STORE_NAME}`
* **Method**: POST
* **Content-Type**: `application/json` or `application/xml`
* **Body**:
```
{
  "displayName": "Example Store",
  "url": "https://store.lab.fiware.org",
  "comment": "Example comment",
  "imageBase64": "PNG_IMAGE_IN_BASE64"
}
```

#### Delete a store

* **Path**: `/api/v2/store/{STORE_NAME}`
* **Method**: DELETE

#### Get a store

* **Path**: `/api/v2/store/{STORE_NAME}`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Content**:
```
{
  "name": "example-store",
  "displayName": "Example Store",
  "url": "https://store.lab.fiware.org",
  "createdAt": 1,
  "comment": "Example comment",
  "creator": "user-1",
  "lasteditor": "user-1",
  "imagePath": "media/store/example-store.png",
  "averageScore": 3.5
}
```

#### List stores

* **Path**: `/api/v2/store`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **limit**: The amount of elements to be retrieved
 * **offset**: The first element to be retrieved
 * **orderBy**: Order used to retrieve the stores (by default: `id`) 
 * **desc**: Descending order (by default: `false`) 
* **Content**:
```
[
 {
  "name": "example-store",
  "displayName": "Example Store",
  "url": "https://store.lab.fiware.org",
  "createdAt": 1,
  "comment": "Example comment",
  "creator": "user-1",
  "lasteditor": "user-1",
  "imagePath": "media/store/example-store.png",
  "averageScore": 3.5
 },
 {
  "name": "example-store-2",
  "displayName": "Example Store 2",
  "url": "https://store2.lab.fiware.org",
  "createdAt": 2,
  "comment": "Example comment 2",
  "creator": "user-2",
  "lasteditor": "user-2",
  "imagePath": "media/store/example-store.png"
  "averageScore": 2.1
 },
 [...]
}
```

#### Review store

* **Path**: `/api/v2/store/{STORE_NAME}/review`
* **Method**: POST
* **Accept**: `application/json` or `application/xml`
* **Body**:
```
{
 "score": 5,
 "comment": "Example Comment"
}
```

#### Update Store Review

* **Path**: `/api/v2/store/{STORE_NAME}/review/{REVIEW_ID}`
* **Method**: POST
* **Accept**: `application/json` or `application/xml`
* **Body**:
```
{
 "score": 5,
 "comment": "Example Comment"
}
```

#### Delete Store Review

* **Path**: `/api/v2/store/{STORE_NAME}/review/{REVIEW_ID}`
* **Method**: DELETE

#### Get Store Review

* **Path**: `/api/v2/store/{STORE_NAME}/review/{REVIEW_ID}`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **detailed**: Set to `true` if you want to retrieve all the information about the user that created the review in just one request (`userName`, `displayName`,...) (by default: `false`)
* **Content**:
```
{
 "id": 1
 "score": 5,
 "user": "fran",
 "comment": "Example Comment",
 "createdAt": 1435248117000,
 "updatedAt": 1435248117000

}
```

#### Get Store Reviews

* **Path**: `/api/v2/store/{STORE_NAME}/review/`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **detailed**: Set to `true` if you want to retrieve all the information about the user that created the review in just one request (`userName`, `displayName`,...) (by default: `false`)
* **Content**:
```
[
 {
  "id": 1
  "score": 5,
  "user": "fran",
  "comment": "Example Comment",
 	"createdAt": 1435248117000,
 	"updatedAt": 1435248117000
 },
  {
  "id": 1
  "score": 2,
  "user": "aitor",
  "comment": "Comment #2",
 	"createdAt": 1435248118000,
 	"updatedAt": 1435248119000
 },
 [...]
]
```



### Descriptions Management API

Descriptions is the way of creating offerings in a Store. A description is just an URL pointing to an Linked USDL file that contains all the offerings that you want to include in the Store. [You can check more about Linked USDL by clicking here](http://linked-usdl.org/).

#### Create a description

* **Path**: `/api/v2/store/{STORE_NAME}/description`
* **Method**: POST
* **Content-Type**: `application/json` or `application/xml`
* **Body**:
```
{
  "displayName": "Example Description",
  "url": "https://repository.lab.fiware.org/pointer_to_linked_usdl.rdf",
}
```

#### Update a description

* **Path**: `/api/v2/store/{STORE_NAME}/description/{DESCRIPTION_NAME}`
* **Method**: POST
* **Content-Type**: `application/json` or `application/xml`
* **Body**:
```
{
  "displayName": "Example Description",
  "url": "https://repository.lab.fiware.org/pointer_to_linked_usdl.rdf",
}
```

#### Delete a description

* **Path**: `/api/v2/store/{STORE_NAME}/description/{DESCRIPTION_NAME}`
* **Method**: DELETE

#### Get a description

* **Path**: `/api/v2/store/{STORE_NAME}/description/{DESCRIPTION_NAME}`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Content**:
```
{
  "name": "example-description",
  "displayName": "Example Description",
  "store": "example-store",
  "url": "https://repository.lab.fiware.org/pointer_to_linked_usdl.rdf",
  "registrationDate": 1,
  "creator": "user-1",
  "lasteditor": "user-1",
  "offerings": [
    {
     "name": "example-offering"
     "displayName": "Example Offering",
     "uri": "https://store.lab.fiware.org/offerings/offering/offering1",
     "description": "Example offering description",
     "version": "1.0",
     "describedIn": {
       "name": "example-description",
       "store": "example-store"
     },
     "imageUrl": "https://store.lab.fiware.org/static/img/offering/offering1.png"
    },
    [...]
  ]
}
```

#### List descriptions in a Store

* **Path**: `/api/v2/store/{STORE_NAME}/description`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **limit**: The amount of elements to be retrieved
 * **offset**: The first element to be retrieved
* **Content**:
```
[ 
 {
  "name": "example-description",
  "displayName": "Example Description",
  "store": "example-store",
  "url": "https://repository.lab.fiware.org/pointer_to_linked_usdl.rdf",
  "registrationDate": 1,
  "creator": "user-1",
  "lasteditor": "user-1",
  "offerings": [...]
 },
 {
  "name": "example-description-2",
  "displayName": "Example Description 2",
  "store": "example-store",
  "url": "https://repository.lab.fiware.org/pointer_to_linked_usdl2.rdf",
  "registrationDate": 2,
  "creator": "user-2",
  "lasteditor": "user-2",
  "offerings": [...]
 }
 [...]
}
```

#### List all descriptions

* **Path**: `/api/v2/descriptions`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **limit**: The amount of elements to be retrieved
 * **offset**: The first element to be retrieved
* **Content**:
```
[ 
 {
  "name": "example-description",
  "displayName": "Example Description",
  "store": "example-store",
  "url": "https://repository.lab.fiware.org/pointer_to_linked_usdl.rdf",
  "registrationDate": 1,
  "creator": "user-1",
  "lasteditor": "user-1",
  "offerings": [...]
 },
 {
  "name": "example-description-2",
  "displayName": "Example Description 2",
  "store": "example-store-1",
  "url": "https://repository.lab.fiware.org/pointer_to_linked_usdl2.rdf",
  "registrationDate": 2,
  "creator": "user-2",
  "lasteditor": "user-2",
  "offerings": [...]
 }
 [...]
}
```

### Offerings API

Each descriptions contains one or more offerings, so you are provided APIs to retrieve the offerings contained in a description.

#### Get an offering

* **Path**: `/api/v2/store/{STORE_NAME}/description/{DESCRIPTION_NAME}/offering/{OFFERING_NAME}`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Content**:
```
{
  "name": "example-offering"
  "displayName": "Example Offering",
  "uri": "https://store.lab.fiware.org/offerings/offering/offering1",
  "description": "Example offering description",
  "version": "1.0",
  "describedIn": {
    "name": "example-description",
    "store": "example-store"
  },
  "imageUrl": "https://store.lab.fiware.org/static/img/offering/offering1.png"
}
```

#### List offerings in a description

* **Path**: `/api/v2/store/{STORE_NAME}/description/{DESCRIPTION_NAME}/offering`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **limit**: The amount of elements to be retrieved
 * **offset**: The first element to be retrieved
* **Content**:
```
[ 
 {
  "name": "example-offering"
  "displayName": "Example Offering",
  "uri": "https://store.lab.fiware.org/offerings/offering/offering1",
  "description": "Example offering description",
  "version": "1.0",
  "describedIn": {
    "name": "example-description",
    "store": "example-store"
  },
  "imageUrl": "https://store.lab.fiware.org/static/img/offering/offering1.png"
 },
 {
  "name": "example-offering-2"
  "displayName": "Example Offering 2",
  "uri": "https://store.lab.fiware.org/offerings/offering/offering2",
  "description": "Another Example offering description",
  "version": "1.0",
  "describedIn": {
    "name": "example-description",
    "store": "example-store"
  },
  "imageUrl": "https://store.lab.fiware.org/static/img/offering/offering2.png"
 }
 [...]
}
```

#### List offerings in a Store

* **Path**: `/api/v2/store/{STORE_NAME}/offering`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **limit**: The amount of elements to be retrieved
 * **offset**: The first element to be retrieved
* **Content**:
```
[ 
 {
  "name": "example-offering"
  "displayName": "Example Offering",
  "uri": "https://store.lab.fiware.org/offerings/offering/offering1",
  "description": "Example offering description",
  "version": "1.0",
  "describedIn": {
    "name": "example-description",
    "store": "example-store"
  },
  "imageUrl": "https://store.lab.fiware.org/static/img/offering/offering1.png"
 },
 {
  "name": "example-offering-2"
  "displayName": "Example Offering 2",
  "uri": "https://store.lab.fiware.org/offerings/offering/offering2",
  "description": "Another Example offering description",
  "version": "1.0",
  "describedIn": {
    "name": "example-description",
    "store": "example-store"
  },
  "imageUrl": "https://store.lab.fiware.org/static/img/offering/offering2.png"
 }
 [...]
}
```

#### List all offerings

* **Path**: `/api/v2/offerings`
* **Method**: GET
* **Accept**: `application/json` or `application/xml`
* **Params**:
 * **limit**: The amount of elements to be retrieved
 * **offset**: The first element to be retrieved
* **Content**:
```
[ 
 {
  "name": "example-offering"
  "displayName": "Example Offering",
  "uri": "https://store.lab.fiware.org/offerings/offering/offering1",
  "description": "Example offering description",
  "version": "1.0",
  "describedIn": {
    "name": "example-description",
    "store": "example-store"
  },
  "imageUrl": "https://store.lab.fiware.org/static/img/offering/offering1.png"
 },
 {
  "name": "example-offering-2"
  "displayName": "Example Offering 2",
  "uri": "https://store.lab.fiware.org/offerings/offering/offering2",
  "description": "Another Example offering description",
  "version": "1.0",
  "describedIn": {
    "name": "example-description",
    "store": "example-store"
  },
  "imageUrl": "https://store.lab.fiware.org/static/img/offering/offering2.png"
 }
 [...]
}
```
