#!/bin/bash

# TODO: Set for your system.
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
export PIG_HOME=/opt/pig-0.16.0/

$PIG_HOME/bin/pig -x local eql.pig
