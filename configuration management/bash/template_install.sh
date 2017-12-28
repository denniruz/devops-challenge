#!/bin/bash
#
# remote execution files must return Script Complete on success or Script Fail on failure.  

function die {
   echo "Script Fail: $1"
	 exit 1
}

sudo apt-get -qq update && sudo apt-get -qq -y install facter > /dev/null || die "apt-get failed"

# get our widget value... we'll use something aws specific as unique per host
X=`facter ec2_instance_id`

if [ -z $X ]; then
	die "facter failed to get info"
fi

# make file
cat > template <<EOF
option 1234
speed 88mph
capacitor_type flux
widget_type $X
model delorean
avoid shopping_mall_parking_lots
EOF

sudo mv template /etc/widgetfile || die "file moved failed"

#done
echo "Script Complete"