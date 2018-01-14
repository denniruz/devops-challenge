#!/bin/bash

echo "------------------------------------------------------------------------------------------------------------------------------"

numLines=`grep "/production/file_metadata/modules/ssh/sshd_config" puppet_access_ssl.log | cat -n | wc -l`
echo "There are $numLines occurances of /production/file_metadata/modules/ssh/sshd_config in the log file."

count=`grep "/production/file_metadata/modules/ssh/sshd_config" puppet_access_ssl.log | cut -d ' ' -f9 | grep -v 200 | cat -n | wc -l`
echo "There are $count occurances of non-200 status code requests to route /production/file_metadata/modules/ssh/sshd_config in the log file."

numNonTwoHun=`cat puppet_access_ssl.log | cut -d ' ' -f9 | grep -v 200 | cat -n | wc -l`
echo "There are $numNonTwoHun occurances of non-200 status code requests in the log file."

putsToDevRep=`grep 'PUT /dev/report' puppet_access_ssl.log | cat -n | wc -l`
echo "There are $putsToDevRep occurances of PUTs to /dev/report in the log file."

ipAddresses=`grep 'PUT /dev/report' puppet_access_ssl.log | cut -d ' ' -f1`
for i in `echo $ipAddresses | uniq`; do
	c=`echo $ipAddresses | grep -c $i`
	printf "The IP address $i made $c PUT requests to /dev/report.\n"
done

echo "------------------------------------------------------------------------------------------------------------------------------"