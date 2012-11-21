1) Install MySQL and create a database "marketplace" for user root. Alternatively you can change the properties\database.properties file
2) Update properties\marketplace.properties according to your environment
3) Run the database\ddl_mysql5.sql script



Basic Operations for Browsing:

User: 		demo1234
Password: 	demo1234

All stores: 					[GET] http://localhost:8080/FiwareMarketplace/v1/registration/stores/
Specific store: 				[GET] http://localhost:8080/FiwareMarketplace/v1/offering/store/[StoreName]
All services for a given store:	[GET] http://localhost:8080/FiwareMarketplace/v1/offering/store/testStore/offerings/
Specific Offering:				[GET] http://localhost:8080/FiwareMarketplace/v1/offering/store/[StoreName]/offering/[OfferingName]
Fulltext Search: 				[GET] http://localhost:8080/FiwareMarketplace/v1/search/offerings/fulltext/[SearchString]


CRUD Operations

User: 		demo1234
Password: 	demo1234

____________________________________________________________
Save User

Save User Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/registration/userManagement/user
Header:
{Accept-Encoding=[gzip, deflate]}
Body:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?><user username="demoCompany"><company>demoCompany</company><email>demo1213@sap.com</email><password>demoCompany</password></user>
Save UserResponse:


Response Status:
201
____________________________________________________________


Find User

Find User Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/registration/userManagement/user/demoCompany
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
null
Find UserResponse:


Response Status:
200
____________________________________________________________


Update User

Update User Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/registration/userManagement/user/demoCompany
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?><user username="demoCompany"><company>demoCompany2</company><email>demo1213@sap.com</email><password>demoCompany</password><registrationDate>2012-06-28T14:29:44+02:00</registrationDate></user>
Update UserResponse:


Response Status:
200
____________________________________________________________

Delete User

Delete User Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/registration/userManagement/user/demoCompany
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
null
Delete UserResponse:

Response Status:
200
________________________________________________________________________________________________________________________
________________________________________________________________________________________________________________________



Save Store


Save Store Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/registration/store
Header:
{Accept-Encoding=[gzip, deflate]}
Body:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?><resource name="testName12356"><url>http://www.test1235.de</url></resource>
Save StoreResponse:


Response Status:
201
____________________________________________________________


Find Store


Find Store Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/registration/store/testName12356
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
null
Find StoreResponse:


Response Status:
200
____________________________________________________________


Update Store


Update Store Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/registration/store/testName12356
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?><resource name="testName12356"><creator username="demo"><company>SAP AG</company><email>demo123@sap.com</email><password>demo</password><registrationDate>2012-06-13T16:43:56+02:00</registrationDate></creator><lasteditor username="demo"><company>SAP AG</company><email>demo123@sap.com</email><password>demo</password><registrationDate>2012-06-13T16:43:56+02:00</registrationDate></lasteditor><registrationDate>2012-06-28T14:41:23+02:00</registrationDate><url>http://www.xxx.de</url></resource>
Update StoreResponse:


Response Status:
200

____________________________________________________________


Delete Store


Delete Store Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/registration/store/testName12356
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
null
Delete StoreResponse:


Response Status:
200
________________________________________________________________________________________________________________________
________________________________________________________________________________________________________________________
Save Service


Save Service Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/offering/store/testStore/offering
Header:
{Accept-Encoding=[gzip, deflate]}
Body:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?><resource name="myService"><url>http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/WarrantyManagementSolution_Master.rdf</url></resource>
Save ServiceResponse:


Response Status:
201
____________________________________________________________


Find Service


Find Service Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/offering/store/testStore/offering/myService
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
null
Find ServiceResponse:


Response Status:
200
____________________________________________________________


Update Service


Update Service Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/offering/store/testStore/offering/myService
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?><resource name="myService2"><creator username="demo"><company>SAP AG</company><email>demo123@sap.com</email><password>demo</password><registrationDate>2012-06-13T16:43:56+02:00</registrationDate></creator><id>14</id><lasteditor username="demo"><company>SAP AG</company><email>demo123@sap.com</email><password>demo</password><registrationDate>2012-06-13T16:43:56+02:00</registrationDate></lasteditor><registrationDate>2012-06-28T14:47:06+02:00</registrationDate><url>http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/WarrantyManagementSolution_Master.rdf</url></resource>
Update ServiceResponse:


Response Status:
200
____________________________________________________________


Delete Service


Delete Service Request:


Resource URL:
http://localhost:8080/FiwareMarketplace/v1/offering/store/testStore/offering/myService2
Header:
{Accept-Encoding=[gzip, deflate], Accept=[application/xml]}
Body:
null
Delete ServiceResponse:


Response Status:
200
____________________________________________________________


