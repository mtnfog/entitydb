#!/bin/bash

if [ "$#" -ne 1 ]; then
	echo "Usage: ./build-image [version]"
	exit 1
fi

VERSION=$1

docker build -t mtnfog/entitydb:$VERSION .
