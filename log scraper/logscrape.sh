#!/bin/bash

#Reports number of GET requests for /production/file_metadata/modules/ssh/sshd_config
echo "Number of GET requests for /sshd_config: `grep -c "GET /production/file_metadata/modules/ssh/sshd_config" ./puppet_access_ssl.log`"

#Reports number of the above that were not 200
echo "Number of GET requests for /sshd_config that were not 200's: `grep "GET /production/file_metadata/modules/ssh/sshd_config" ./puppet_access_ssl.log | grep -cv "200"`"

#Reports number of non 200's
echo "Number of non-200's from Apache: `grep -cv "200" ./puppet_access_ssl.log`"

#Reports number of PUT requests that went to /dev/report
echo "Number of PUT requests for /dev/report: `grep -c "PUT /dev/report" ./puppet_access_ssl.log`"

#Reports number of times each IP made a PUT request to /dev/report by placing results into a file named ipreport and then parses those results
echo -e "List of IPs that have PUT to /dev/report:\n`grep "PUT /dev/report" ./puppet_access_ssl.log | egrep -o '[[:digit:]]{1,3}\.[[:digit:]]{1,3}\.[[:digit:]]{1,3}\.[[:digit:]]{1,3}' > ipreport`"
sort ./ipreport | uniq -c
