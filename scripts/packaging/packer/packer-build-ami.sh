#!/bin/bash

#
# Copyright 2024 Philterd, LLC
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

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

