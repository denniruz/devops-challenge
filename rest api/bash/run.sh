#!/bin/bash
#
# file distro
function die {
	# like perl die
	echo $1
	exit 1
}

# simple argument flag handling
if [ $# -lt 1 ];then
	die "Run with -h for help"
fi

while test $# -gt 0; do
   case "$1" in
      -h)
         echo "$0 - Push script and execute"
         echo " "
         echo "$0 [-h] -k <key file> -t <hosts file> -u <remote user> -s <script file>"
         echo " "
         echo "options:"
         echo "-h                show brief help"
				 echo "-u username       remote username"
         echo "-k keyfile        which ssh key to use"
         echo "-t hostfile       file with list of hosts"
				 echo "-s script         script to send and execute"
         exit 0
         ;;
			-u) 
			   shift
			   if test $# -gt 0; then
					  userName=$1
				 else
					  die "No user specified"
				 fi
				 shift
				 ;;
      -k)
         shift
         if test $# -gt 0; then
					  keyFile=$1
         else
            die "no file specified"
         fi
         shift
         ;;
		  -t)
				 shift
         if test $# -gt 0; then
					  hostFile=$1
         else
            die "no file specified"
         fi
         shift
         ;;
			-s)
				 shift
         if test $# -gt 0; then
					  scriptFile=$1
         else
            die "no file specified"
         fi
         shift
         ;;
      esac
done

# collect keys.  We'll just do this every time instead of searching for changes
echo "#-> Getting host keys..."
ssh-keyscan -f $hostFile > ssh_known_hosts 2> /dev/null || die "ssh-keyscan failed"

failCount=0
successCount=0

# execute remote job, cleanup after
for h in `cat $hostFile`
do
	 echo "#-> Pushing $scriptFile to $h..."
   scp -i $keyFile -o UserKnownHostsFile=ssh_known_hosts $scriptFile $userName@$h:
   scp -i $keyFile -o UserKnownHostsFile=ssh_known_hosts rest.sh $userName@$h:
   scp -i $keyFile -o UserKnownHostsFile=ssh_known_hosts http $userName@$h:
	 echo "#-> Executing $scriptFile on $h..."
   remoteOutput=`ssh -i $keyFile -o UserKnownHostsFile=ssh_known_hosts $userName@$h "./$scriptFile && rm ./$scriptFile" `
	 test `echo $remoteOutput | grep -c Fail` -gt 0 && ((failCount++))
	 test `echo $remoteOutput | grep -c Complete` -gt 0 && ((successCount++))
done

echo "############################"
echo "## Successful Runs: $successCount"
echo "##     Failed Runs: $failCount"
