# Utilities

This folder contains utilities that can be used to install WMarket in a easier
way. To install WMarket, you can use these to scripts:

* `install.sh`:
 * Ready for Ubuntu 14.04 LTS & CentOS 7
 * It installs all the required dependencies
 * It requires interaction to set the following parameters:
   * **Database**: user name and password
   * **Index**: path to Store Lucene indexes
   * **Media**: path to Store media files and the maximum size of these files
   * **Autoupdate period**: period to upload the descriptions and retrieve new
     offerings
   * **OAuth2**: enable or disable OAuth2. If OAuth2 is enabled, some parameters
     will be required (IdM URL, client ID, client secret, machine IP...)
* `autoinstall.sh`:
  * Ready for Ubuntu 14.04 LTS
  * It installs all the required dependencies
  * It does not require interaction. Parameters are set with default values:
    * **Database**:
      * User: `root` 
      * Password: `admin`
    * **Index**: `/opt/index`
    * **Media**:
      * Path: `/opt/media`
      * Max Size: 3145728 (3 MB)
    * **Autoupdate period**: 43200 (1 day)
    * **OAuth2**: No

Additionally, you can use the `test.sh` script to check if the service is
properly running. The script needs to know the IP where the service is running.
You can specify it by setting the `IP` environment variable. For example, if
your instance is running on `localhost`, you can run the script by executing the
following commands:

```
export IP=127.0.0.1
./test.sh
```
