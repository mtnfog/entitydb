#!/bin/bash

# Shows all indexed entities.
curl -X GET "https://search-entitydb-4cpoahp2k7dpakwchunuuxrcwm.us-east-1.es.amazonaws.com/entities/entity/_search"

# Shows only person entities.
#curl -X GET "https://search-entitydb-4cpoahp2k7dpakwchunuuxrcwm.us-east-1.es.amazonaws.com/entities/entity/_search?q=text:'George+Washington'"

