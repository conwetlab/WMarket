# WMarket Docker Image

Stating on version 4.3.3, you are able to run WMarket with Docker. As you may know, WMarket needs a MySQL database to store some information. For this reason, you must create an additional container to run the database. You can do it automatically with `docker-compose` or manually by following the given steps.

## Automatically

You can install WMarket automatically if you have `docker-compose` installed in your machine. To do so, you must create a folder to place a new file file called `docker-compose.yml` that should include the following content:

```
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
```

Once that you have created the file, run the following command:

```
docker-compose up
```

Then, WMarket should be up and running in `http://YOUR_HOST:80/WMarket` replacing `YOUR_HOST` by the host of your machine.

## Manually

### 1) Creating a Container to host the Database

The first thing that you have to do is to create a docker container that will host the database used by WMarket. To do so, you can execute the following command:

```
docker run --name wmarket_db -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=marketplace -v /var/lib/mysql -d mysql
```

* As can be seen, some environment variables are set in this command to set up the data base. You must **not** change these variables, since their values are the ones expected by the WMarket image.

### 2) Deploying the WMarket Image

Once that the database is configured, you can deploy the image by running the following command (*replace `PORT` by the port of your local machine that will be used to access the service*):

```
docker run --name wmarket -v /WMarket/static -p PORT:8080 --link wmarket_db -d conwetlab/wmarket
```

Once that you have run these commands, WMarket should be up and running in `http://YOUR_HOST:PORT/WMarket` replacing `YOUR_HOST` by the host of your machine and `PORT` by the port selected in the previous step. 
