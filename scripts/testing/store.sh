#!/bin/bash
ACL="user:group:1"
curl -vvvv -X POST -H "Content-Type: application/json" -H "Authorization: asdf1234" --data @body.json "http://$1:8080/api/entity?context=c&documentId=d&acl=$ACL"

