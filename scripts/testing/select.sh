#!/bin/bash
#curl -vvvv -H "Authorization: asdf1234" "http://$1:8080/api/eql?query=select+%2A+from+entities+where+type+=+%22PERSON%22"
curl -vvvv -H "Authorization: asdf1234" "http://$1:8080/api/eql?query=select+%2A+from+entities+where+text+=+%22George+Washington%22"

