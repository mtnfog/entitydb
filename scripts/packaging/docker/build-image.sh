#!/bin/bash

echo "Getting the version from the pom.xml."
VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`

docker build -t mtnfog/entitydb:$VERSION .
