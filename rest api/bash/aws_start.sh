#!/bin/bash
#
# creates aws environment for testing Devops Challenge.  Minimal VPC, some Ubuntu hosts.

#aws profile for aws cli.. could be an argument
PROFILE="devwest"

# string for key name, could be an argument later.
KEY="asdevchal"

# ec2 instance count, ami, could be arguments
# uswest2 - ami-0def3275
# useast1 - ami-aa2ea6d0
COUNT=1
AMI="ami-0def3275"

#############################

function die {
  # like perl, scream then exit
	echo $1
	exit 1
}
# need this later
CIDR=`curl -s curl https://api.ipify.org | sed -e 's/$/\/32/'`

if [ -f ./aws.out ]; then
	# this has been run before
	die "Existing VPC detected, fix that"
fi

#############################
# generate a key for this project
if [ ! -f ./$KEY ] || [ ! -f ./$KEY.pub ]; then
	openssl genrsa -out $KEY.pem 2048 || die "Failed to generate cert"
  KEYMAT=`openssl rsa -in $KEY.pem -pubout | egrep -v '^--' | tr -d '\n'`
fi
# read public key in
aws ec2 import-key-pair --profile $PROFILE --key-name $KEY --public-key-material  "$KEYMAT" || die "Failed to import key"
# fix perms
chmod 600 $KEY.pem

echo "Key imported..."

#############################
# make a simple vpc
vpcId=`aws ec2 create-vpc --profile $PROFILE --cidr-block 10.0.0.0/16 --query 'Vpc.VpcId' --output text` || die "Failed to create vpc"
echo "$vpcId" >> aws.out

aws ec2 create-tags --profile $PROFILE --resources $vpcId --tags Key=Name,Value="devops_challenge_as" || die "Failed in vpc tag"
aws ec2 modify-vpc-attribute --profile $PROFILE --vpc-id $vpcId --enable-dns-support "{\"Value\":true}" || die "Failed dns-support"
aws ec2 modify-vpc-attribute --profile $PROFILE --vpc-id $vpcId --enable-dns-hostnames "{\"Value\":true}" || die "Failed dns hostnames"

echo "VPC created..."

# with an igw
internetGatewayId=`aws ec2 create-internet-gateway --profile $PROFILE --query 'InternetGateway.InternetGatewayId' --output text` || die "Failed to create IGW"

aws ec2 attach-internet-gateway --profile $PROFILE --internet-gateway-id $internetGatewayId --vpc-id $vpcId || die "Failed to attach IGW"
aws ec2 create-tags --profile $PROFILE --resources $internetGatewayId --tags Key=Name,Value="devops_challenge_as" || die "Failed in igw tag"

echo "IGW created, attached..."

# and a single subnet/routes
subnetID=`aws ec2 create-subnet --profile $PROFILE --vpc-id $vpcId --cidr-block 10.0.0.0/24 --availability-zone us-west-2c --query 'Subnet.SubnetId' --output text`  || die "Failed to create subnet"
aws ec2 create-tags --profile $PROFILE --resources $subnetID --tags Key=Name,Value="devops_challenge_as_public" || die "Failed to tag subnet"

echo "Subnet created..."

rtID=`aws ec2 describe-route-tables --profile $PROFILE --filters "Name=vpc-id,Values=$vpcId" --query 'RouteTables[*].RouteTableId' --output text`
aws ec2 create-route --profile $PROFILE --route-table-id $rtID --destination-cidr-block 0.0.0.0/0 --gateway-id $internetGatewayId || die "Failed to add default route"
aws ec2 associate-route-table --profile $PROFILE --route-table-id $rtID --subnet-id $subnetID || die "Route table association failure"

echo "Route table associated, default route set..."

# basic sec grp
sgID=`aws ec2 create-security-group --profile $PROFILE --group-name devops_challenge_as_sg --description "SG for Devops Challege" --vpc-id $vpcId --query GroupId --output text`  || die "Failed to create security group"
aws ec2 create-tags --profile $PROFILE --resources $sgID --tags Key=Name,Value="SG for Devops Challege" || die "Faled to tg security group"
aws ec2 authorize-security-group-ingress --profile $PROFILE --group-id $sgID --protocol tcp --port 22 --cidr $CIDR || die "Failed on ingress rule"
aws ec2 authorize-security-group-ingress --profile $PROFILE --group-id $sgID --protocol tcp --port 80 --cidr $CIDR || die "Failed on ingress rule"

echo "Security Group created..."

#############################
# finally make hosts
aws ec2 run-instances --profile $PROFILE --image-id $AMI --count $COUNT --subnet-id $subnetID --instance-type t2.micro  --key-name $KEY --security-group-ids $sgID --associate-public-ip-address --tag-specifications 'ResourceType=instance,Tags=[{Key="Name",Value="devchall"}]' || die "Failed to make ec2 instances"

echo "Instances created..."

sleep 20 # give them time to spin up, and public names made.. should be a test loop

# jq instaed of --query, to ouput list instead of single line
aws ec2 describe-instances --profile $PROFILE --filters "Name=vpc-id,Values=$vpcId" | jq '.Reservations[].Instances[].PublicDnsName' | sed -e 's/"//g' > hosts

echo "Instance inventory complete."

