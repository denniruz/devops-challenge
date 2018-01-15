#!/bin/bash
#
# deletes aws environment for testing Devops Challenge. 

#aws profile
PROFILE="devwest"

function die {
  # like perl, scream then exit
	echo $1
	exit 1
}

# get vpc from startup run
if [ -f aws.out ];then
  vpcId=`cat aws.out`
else
	die "Can't found the VPC token, exiting"
fi

# get key used from one of the ec2 instances
instanceKey=`aws ec2 describe-instances --profile $PROFILE --filters "Name=vpc-id,Values=$vpcId"  --query 'Reservations[*].Instances[0].KeyName' --output text`

echo "Found the key..."

# kill the test instances
instanceList=`aws ec2 describe-instances --profile $PROFILE --filters "Name=vpc-id,Values=$vpcId"  --query 'Reservations[*].Instances[*].InstanceId' --output text`
aws ec2 terminate-instances --profile=devwest --instance-ids $instanceList || die "Failed to terminate instances: $instanceList"

echo -n "Instances terminating..."

# must wait for instances to actually be terminated
while true
do
	aws ec2 describe-instances --profile $PROFILE --filters "Name=vpc-id,Values=$vpcId"  --query 'Reservations[*].Instances[*].InstanceId' --output text | grep 'i-' > /dev/null || break
	sleep 5
done

echo "done"

# from aws cli, must manually remove all dependencies
sgList=`aws ec2 describe-security-groups --profile $PROFILE --filters "Name=vpc-id,Values=$vpcId" "Name=group-name,Values=devops*"  --query 'SecurityGroups[*].GroupId' --output text`
sleep 5 # insurance on instance shutdown
for i in $sgList
do
	aws ec2 delete-security-group --profile $PROFILE --group-id $i || die "Can't delete security group $i"
done
echo "Security groups removed..."

subList=`aws ec2 describe-subnets --profile $PROFILE --filters "Name=vpc-id,Values=$vpcId" --query 'Subnets[*].SubnetId' --output text`
for i in $subList
do
	aws ec2 delete-subnet --profile $PROFILE --subnet-id $i || die "Can't delete subnet $i"
done
echo "Subnets removed..."

rtId=`aws ec2 describe-route-tables --profile $PROFILE --filters "Name=vpc-id,Values=$vpcId" --query 'RouteTables[*].RouteTableId' --output text`
aws ec2 delete-route --profile $PROFILE --route-table-id $rtId --destination-cidr-block 0.0.0.0/0

gwList=`aws ec2 describe-internet-gateways --profile $PROFILE --filters "Name=attachment.vpc-id,Values=$vpcId" --query 'InternetGateways[*].InternetGatewayId' --output text`
aws ec2 detach-internet-gateway --profile $PROFILE --internet-gateway-id $gwList --vpc-id $vpcId || die "Could not detach IGW"
aws ec2 delete-internet-gateway --profile $PROFILE --internet-gateway-id $gwList || die "Could not delete IGW $gwList"
echo "Removed IGW..."

aws ec2 delete-vpc --profile $PROFILE --vpc-id=$vpcId || die "Could not delete vpc: $vpcId"

echo "VPC deleted..."

# clean up the key pair used
aws ec2 delete-key-pair --profile $PROFILE --key-name $instanceKey || die "Could not delete key pair : $instanceKey"
rm $instanceKey.pem || die "Could not remove $instanceKey.pem"
echo "Key pair deleted..."

# and, finally, delete the local list of instances
rm hosts || die "Could not remove host file"
rm aws.out || die "Could not delete aws.out.. wtf?"
rm ssh_known_hosts || die "Could not delete the known_hosts file"

echo "Inventory deleted..... All done"