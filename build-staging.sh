#!/bin/bash

# STAGING ENVIRONMENT BUILD SCRIPT

#rm -rf build/

# Removes the running container
#sudo docker rm --force $(sudo docker ps -a -q)
#sudo docker volume rm $(sudo docker volume ls -qf dangling=true)
#sudo docker rmi $(sudo docker images | grep '^<none>' | awk '{print $3}')

# Resets the configuration
#git reset --hard HEAD

#git fetch && git pull
#cat secrets-integration.yml >> src/main/resources/application-integration.yml

# Build and deploy to a new docker container
gradle clean build -x test
sudo docker build --build-arg version=0.0.1-SNAPSHOT --build-arg environment=integration -t patogalla-api:lastest .
sudo docker run -d -p "8080:8080" patogalla-api
