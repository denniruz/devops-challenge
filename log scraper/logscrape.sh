#!/bin/bash

echo "sshd_config was fetched: `grep -c /production/file_metadata/modules/ssh/sshd_config puppet_access_ssl.log` times"

echo "Of those, `grep '/production/file_metadata/modules/ssh/sshd_config' puppet_access_ssl.log | grep -v -c 'HTTP[^"]*" 200'` returned a code other than 200"

echo "Apache returned a code other than 200 `grep -c -v 'HTTP[^"]*" 200' puppet_access_ssl.log` times"

echo "There were PUT requests to report `grep -c 'PUT /dev/report' puppet_access_ssl.log` times"

echo "Breakdown of PUT request accesses:"
grep 'PUT /dev/report' puppet_access_ssl.log | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}' | uniq -c
