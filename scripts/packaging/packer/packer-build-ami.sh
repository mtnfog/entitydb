#!/bin/bash

# This script executes packer with the following parameters.

# Note: You need to edit the packer script entitydb.json to modify
# the security_group_id, subnet_id, and vpc_id for your environment.

# Note: The AMI will have AWS CloudWatch Logs enabled for the system
# log and for the EntityDB log. Make sure EntityDB is running under an
# IAM role that permits access to CloudWatch logs.

echo "Getting the version from the pom.xml."
VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`

JAR="../../../entitydb-app/target/entitydb.jar"
LICENSE="../../../LICENSE"
README="../../../README.md"
CONF="../../../entitydb-app/target/entitydb.conf"
PROPS="../../../entitydb-app/target/entitydb.properties"
MAPPING="../../../entitydb-app/target/mapping.json"
TIMESTAMP=`date "+%Y.%m.%d-%H.%M.%S"`
PACKER_LOG=1 /opt/packer build -only amazon-ebs \
	-var "jar=$JAR" \
	-var "license=$LICENSE" \
	-var "readme=$README" \
	-var "conf=$CONF" \
	-var "props=$PROPS" \
	-var "mapping=$MAPPING" \
	-var "ami_name=EntityDB $VERSION $TIMESTAMP" \
	./entitydb.json

