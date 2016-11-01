#!/bin/bash
IMAGE=`docker images | grep 'mtnfog/entitydb' | awk -e '{print $3}'`
docker run -p 8080:8080 -p 9022:22 -d --name entitydb $IMAGE
