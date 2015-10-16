#!/bin/bash

# The script is aborted if any command fails. If it is OK that a comand fails,
# use ./mycomand || true
set -e

# Checks that the service is up and running.
# If the service has not been deployed, the server will return 404 and the command will fail
# If the server cannot connect with the DB, the server will return 500 and the command will fail 
wget http://$IP:8080/WMarket/api/v2/user -o /dev/null