#!/bin/bash

echo "sshd_config was fetched: `grep -c /production/file_metadata/modules/ssh/sshd_config $1` times"

echo "Of those, `grep '/production/file_metadata/modules/ssh/sshd_config' $1 | grep -v -c 'HTTP[^"]*" 200'` returned a code other than 200"

echo "Apache returned a code other than 200 `grep -c -v 'HTTP[^"]*" 200' $1` times"

echo "There were PUT requests to report `grep -c 'PUT /dev/report' $1` times"

echo "Breakdown of PUT request accesses:"
grep 'PUT /dev/report' $1 | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}' | uniq -c
