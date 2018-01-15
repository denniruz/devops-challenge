#!/bin/bash
#
# remote execution files must return Script Complete on success or Script Fail on failure.  

function die {
   echo "Script Fail: $1"
	 exit 1
}

sudo apt-get -qq update && sudo apt-get -qq -y install xinetd jq  > /dev/null || die "apt-get failed"

# get xinetd config in place
if [ -e http ];then
   sudo chown root:root http
else
   die "xinetd config for http isn't here!"
fi

if [ ! -e /etc/xinetd.d/http ]; then
   sudo mv http /etc/xinetd.d/ || die "file moved failed"
   sudo service xinetd restart
fi

# we're just running this in the ubuntu homedir
if [ -e rest.sh ];then
	chmod 755 rest.sh
else
	die "Rest API executable is missing"
fi

#done
echo "Script Complete"
